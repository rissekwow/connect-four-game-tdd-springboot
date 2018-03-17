package wt.connectfourgame.model;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.WithAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.fixtures.CellFixture;
import wt.connectfourgame.model.boards.Board;
import wt.connectfourgame.model.boards.Board7x6;
import wt.connectfourgame.model.states.Cell;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;

public class Board7x6Test implements WithAssertions, WithBDDMockito {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private Map<Integer, List<Cell>> boardCols;

	@InjectMocks
	private Board7x6 board;

	private final CellState R = CellState.RED;
	private final CellState Y = CellState.YELLOW;
	private final CellState E = CellState.EMPTY;

	@Test
	public void isBoardFilledWithEmptyCells() {
		Board board = new Board7x6();
		assertThat(board.cellBoardToList()).filteredOn(cell -> cell.getCellState().equals(CellState.EMPTY)).hasSize(42);
	}

	@Test
	public void isBoardColsInitForAllColTypes() {
		Board board = new Board7x6();
		assertThat(board.getBoardCols().keySet()).containsExactly(0, 1, 2, 3, 4, 5, 6);
		assertThat(board.getBoardCols()).allSatisfy((col, list) -> {
			assertThat(list).extracting(c -> c.getCellState()).containsExactly(E, E, E, E, E, E);
		});
	}

	@Test
	public void isAddToColAvailable() {
		// given
		List<Cell> cellList = CellFixture.generateCellList(R, Y, R, Y, R, E);
		// when
		when(boardCols.get(0)).thenReturn(cellList);
		// then
		assertThat(board.isAddToColAvailable(0)).isEqualTo(true);
	}

	@Test
	public void isAddToColNonAvailable() {
		// given
		List<Cell> cellList = CellFixture.generateCellList(R, Y, R, Y, R, Y);
		// when
		when(boardCols.get(0)).thenReturn(cellList);
		// then
		assertThat(board.isAddToColAvailable(0)).isEqualTo(false);
	}

	@Test
	public void addCell() throws CellColumnIsFullException {
		// given
		List<Cell> cellList = CellFixture.generateCellList(E, E, E, E, E, E);
		// when
		when(boardCols.get(0)).thenReturn(cellList);
		board.addCell(0, Y);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, E, E, E, E, E);

		board.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, E, E, E, E);

		board.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, E, E, E);

		board.addCell(0, Y);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, Y, E, E);

		board.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, Y, R, E);

		board.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, Y, R, R);

		assertThatThrownBy(() -> {
			board.addCell(0, Y);
		}).isInstanceOf(CellColumnIsFullException.class)
				.hasMessageContaining("Cell columns is filled with unempty cells");
	}

	@Test
	public void isStateOpen() {
		Board board = new Board7x6();
		assertThat(board.getGameState()).isEqualTo(GameState.OPEN);
	}

	@Test
	public void isStateDraw() {
		// given
		List<Cell> cellList1 = CellFixture.generateCellList(Y, R, Y, R, Y, Y);
		List<Cell> cellList2 = CellFixture.generateCellList(R, Y, Y, Y, R, R);
		List<Cell> cellList3 = CellFixture.generateCellList(R, Y, Y, R, R, R);
		List<Cell> cellList4 = CellFixture.generateCellList(Y, R, Y, R, Y, R);
		List<Cell> cellList5 = CellFixture.generateCellList(Y, R, Y, R, Y, Y);
		List<Cell> cellList6 = CellFixture.generateCellList(Y, Y, R, Y, Y, R);
		List<Cell> cellList7 = CellFixture.generateCellList(R, R, R, Y, R, Y);
		// when
		when(boardCols.get(0)).thenReturn(cellList1);
		when(boardCols.get(1)).thenReturn(cellList2);
		when(boardCols.get(2)).thenReturn(cellList3);
		when(boardCols.get(3)).thenReturn(cellList4);
		when(boardCols.get(4)).thenReturn(cellList5);
		when(boardCols.get(5)).thenReturn(cellList6);
		when(boardCols.get(6)).thenReturn(cellList7);
		// then
		assertThat(board.getGameState()).isEqualTo(GameState.DRAW);
	}

	@Test
	public void isGameStateRedWin() {
		// given
		List<Cell> cellList1 = CellFixture.generateCellList(R, E, E, E, E, E);
		List<Cell> cellList2 = CellFixture.generateCellList(Y, R, Y, Y, R, E);
		List<Cell> cellList3 = CellFixture.generateCellList(Y, Y, R, E, E, E);
		List<Cell> cellList4 = CellFixture.generateCellList(R, R, Y, R, E, E);
		List<Cell> cellList5 = CellFixture.generateCellList(Y, R, E, E, E, E);
		List<Cell> cellList6 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList7 = CellFixture.generateCellList(E, E, E, E, E, E);
		// when
		when(boardCols.get(0)).thenReturn(cellList1);
		when(boardCols.get(1)).thenReturn(cellList2);
		when(boardCols.get(2)).thenReturn(cellList3);
		when(boardCols.get(3)).thenReturn(cellList4);
		when(boardCols.get(4)).thenReturn(cellList5);
		when(boardCols.get(5)).thenReturn(cellList6);
		when(boardCols.get(6)).thenReturn(cellList7);
		// then
		assertThat(board.getGameState()).isEqualTo(GameState.RED_WIN);
	}

	@Test
	public void isGameStateYellowWin() {
		// given
		List<Cell> cellList1 = CellFixture.generateCellList(Y, Y, Y, Y, E, E);
		List<Cell> cellList2 = CellFixture.generateCellList(R, R, R, E, E, E);
		List<Cell> cellList3 = CellFixture.generateCellList(R, E, E, E, E, E);
		List<Cell> cellList4 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList5 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList6 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList7 = CellFixture.generateCellList(E, E, E, E, E, E);
		// when
		when(boardCols.get(0)).thenReturn(cellList1);
		when(boardCols.get(1)).thenReturn(cellList2);
		when(boardCols.get(2)).thenReturn(cellList3);
		when(boardCols.get(3)).thenReturn(cellList4);
		when(boardCols.get(4)).thenReturn(cellList5);
		when(boardCols.get(5)).thenReturn(cellList6);
		when(boardCols.get(6)).thenReturn(cellList7);
		// then
		assertThat(board.getGameState()).isEqualTo(GameState.YELLOW_WIN);
	}

}
