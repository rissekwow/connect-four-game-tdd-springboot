package wt.connectfourgame.model.boards;

import java.util.List;
import java.util.Map;

import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.model.states.Cell;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;

public interface Board {

	List<Cell> cellBoardToList();

	Map<Integer, List<Cell>> getBoardCols();

	GameState getGameState();

	void addCell(int colNumber, CellState color) throws CellColumnIsFullException;

	boolean isAddToColAvailable(int colNumber);

}
