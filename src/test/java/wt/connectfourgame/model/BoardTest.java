package wt.connectfourgame.model;

import org.assertj.core.api.WithAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import info.solidsoft.mockito.java8.api.WithBDDMockito;

public class BoardTest implements WithAssertions, WithBDDMockito{

	@Rule 
	public MockitoRule rule = MockitoJUnit.rule();
	
	@Test
	public void isBoardFilledWithEmptyCells() {
		Board board = new Board();
		assertThat(board.getCellBoard()).filteredOn(cell -> cell.getCellState().equals(CellState.EMPTY)).hasSize(81);
	}

}
