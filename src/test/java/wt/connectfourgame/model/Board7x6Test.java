package wt.connectfourgame.model;

import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.model.boards.Board;
import wt.connectfourgame.model.boards.Board7x6;
import wt.connectfourgame.model.states.CellState;

public class Board7x6Test implements WithAssertions, WithBDDMockito {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

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
			assertThat(list).extracting(c -> c.getCellState()).containsExactly(CellState.EMPTY, CellState.EMPTY,
					CellState.EMPTY, CellState.EMPTY, CellState.EMPTY, CellState.EMPTY);
		});
	}

	@Test
	@Ignore
	public void isAddToColAvailable() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void addCell() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void getGameState() {
		fail("Not yet implemented");
	}

}
