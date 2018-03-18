package wt.connectfourgame.model;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
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
	private Board7x6 boardMock;

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
	public void isAddToColAvailable() throws InvalidColumnNumberException {
		// given
		List<Cell> cellList = CellFixture.generateCellList(R, Y, R, Y, R, E);
		// when
		when(boardCols.get(0)).thenReturn(cellList);
		// then
		assertThat(boardMock.isAddToColAvailable(0)).isEqualTo(true);
	}

	@Test
	public void isAddToColNonAvailable() throws InvalidColumnNumberException {
		// given
		List<Cell> cellList = CellFixture.generateCellList(R, Y, R, Y, R, Y);
		// when
		when(boardCols.get(0)).thenReturn(cellList);
		// then
		assertThat(boardMock.isAddToColAvailable(0)).isEqualTo(false);
	}

	@Test
	public void isAddToColThrowInvalidColumnNumberException() {
		Board board = new Board7x6();
		assertThatThrownBy(() -> {
			board.isAddToColAvailable(80);
		}).isInstanceOf(InvalidColumnNumberException.class)
				.hasMessageContaining("The column number [80] does not exist.");
	}

	@Test
	public void addCell() throws CellColumnIsFullException, InvalidColumnNumberException {
		// given
		List<Cell> cellList = CellFixture.generateCellList(E, E, E, E, E, E);
		// when
		when(boardCols.get(0)).thenReturn(cellList);
		boardMock.addCell(0, Y);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, E, E, E, E, E);

		boardMock.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, E, E, E, E);

		boardMock.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, E, E, E);

		boardMock.addCell(0, Y);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, Y, E, E);

		boardMock.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, Y, R, E);

		boardMock.addCell(0, R);
		// then
		assertThat(boardCols.get(0)).extracting(c -> c.getCellState()).containsExactly(Y, R, R, Y, R, R);

		assertThatThrownBy(() -> {
			boardMock.addCell(0, Y);
		}).isInstanceOf(CellColumnIsFullException.class)
				.hasMessageContaining("Cell columns is filled with unempty cells.");
	}

	@Test
	public void isAddCellThrowInvalidColumnNumberException() {
		Board board = new Board7x6();
		assertThatThrownBy(() -> {
			board.addCell(80, Y);
		}).isInstanceOf(InvalidColumnNumberException.class)
				.hasMessageContaining("The column number [80] does not exist.");
	}

	@Test
	@Ignore
	public void isStateOpen() {
		Board board = new Board7x6();
		assertThat(board.getGameState()).isEqualTo(GameState.OPEN);
	}

	@Test
	public void isGameStateDraw() throws CellColumnIsFullException, InvalidColumnNumberException {
		Board board = new Board7x6();

		List<Cell> cellList1 = CellFixture.generateCellList(Y, R, Y, R, Y, Y);
		List<Cell> cellList2 = CellFixture.generateCellList(R, Y, Y, Y, R, R);
		List<Cell> cellList3 = CellFixture.generateCellList(R, Y, Y, R, R, R);
		List<Cell> cellList4 = CellFixture.generateCellList(Y, R, Y, R, Y, R);
		List<Cell> cellList5 = CellFixture.generateCellList(Y, R, Y, R, Y, Y);
		List<Cell> cellList6 = CellFixture.generateCellList(Y, Y, R, Y, Y, R);
		List<Cell> cellList7 = CellFixture.generateCellList(R, R, R, Y, R, Y);

		for (Cell cell : cellList1)
			board.addCell(0, cell.getCellState());

		for (Cell cell : cellList2)
			board.addCell(1, cell.getCellState());

		for (Cell cell : cellList3)
			board.addCell(2, cell.getCellState());

		for (Cell cell : cellList4)
			board.addCell(3, cell.getCellState());

		for (Cell cell : cellList5)
			board.addCell(4, cell.getCellState());

		for (Cell cell : cellList6)
			board.addCell(5, cell.getCellState());

		for (Cell cell : cellList7)
			board.addCell(6, cell.getCellState());

		// then
		assertThat(board.getGameState()).isEqualTo(GameState.DRAW);
	}

	@Test
	public void isGameStateRedWin() throws CellColumnIsFullException, InvalidColumnNumberException {
		Board board = new Board7x6();

		List<Cell> cellList1 = CellFixture.generateCellList(R, E, E, E, E, E);
		List<Cell> cellList2 = CellFixture.generateCellList(Y, R, Y, Y, R, E);
		List<Cell> cellList3 = CellFixture.generateCellList(Y, Y, R, E, E, E);
		List<Cell> cellList4 = CellFixture.generateCellList(R, R, Y, R, E, E);
		List<Cell> cellList5 = CellFixture.generateCellList(Y, R, E, E, E, E);
		List<Cell> cellList6 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList7 = CellFixture.generateCellList(E, E, E, E, E, E);
		// when
		for (Cell cell : cellList1)
			board.addCell(0, cell.getCellState());

		for (Cell cell : cellList2)
			board.addCell(1, cell.getCellState());

		for (Cell cell : cellList3)
			board.addCell(2, cell.getCellState());

		for (Cell cell : cellList4)
			board.addCell(3, cell.getCellState());

		for (Cell cell : cellList5)
			board.addCell(4, cell.getCellState());

		for (Cell cell : cellList6)
			board.addCell(5, cell.getCellState());

		for (Cell cell : cellList7)
			board.addCell(6, cell.getCellState());
		assertThat(board.getGameState()).isEqualTo(GameState.RED_WIN);
	}

	@Test
	public void isGameStateYellowWin() throws CellColumnIsFullException, InvalidColumnNumberException {
		Board board = new Board7x6();
		// given
		List<Cell> cellList1 = CellFixture.generateCellList(Y, Y, Y, Y, E, E);
		List<Cell> cellList2 = CellFixture.generateCellList(R, R, R, E, E, E);
		List<Cell> cellList3 = CellFixture.generateCellList(R, E, E, E, E, E);
		List<Cell> cellList4 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList5 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList6 = CellFixture.generateCellList(E, E, E, E, E, E);
		List<Cell> cellList7 = CellFixture.generateCellList(E, E, E, E, E, E);
		// when
		for (Cell cell : cellList1)
			board.addCell(0, cell.getCellState());

		for (Cell cell : cellList2)
			board.addCell(1, cell.getCellState());

		for (Cell cell : cellList3)
			board.addCell(2, cell.getCellState());

		for (Cell cell : cellList4)
			board.addCell(3, cell.getCellState());

		for (Cell cell : cellList5)
			board.addCell(4, cell.getCellState());

		for (Cell cell : cellList6)
			board.addCell(5, cell.getCellState());

		for (Cell cell : cellList7)
			board.addCell(6, cell.getCellState());
		assertThat(board.getGameState()).isEqualTo(GameState.YELLOW_WIN);
	}

}
