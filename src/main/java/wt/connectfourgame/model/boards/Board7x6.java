package wt.connectfourgame.model.boards;

import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;

public class Board7x6 extends BoardTemplate {

	private static final int xSize = 7;
	private static final int ySize = 6;

	public Board7x6() {
		super(xSize, ySize);
		initCellBoard();
		initBoardCols();
	}

	public boolean isAddToColAvailable(int colNumber) throws InvalidColumnNumberException {
		return false;
	}

	public void addCell(int colNumber, CellState color) throws CellColumnIsFullException, InvalidColumnNumberException {

	}

	public GameState getGameState() {
		return null;
	}

}
