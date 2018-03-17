package wt.connectfourgame.model.boards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
import wt.connectfourgame.model.states.Cell;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;

public abstract class BoardTemplate implements Board {

	private int xSize;
	private int ySize;

	protected Cell[][] cellBoard;
	protected Map<Integer, List<Cell>> boardCols;

	@Override
	public abstract boolean isAddToColAvailable(int colNumber) throws InvalidColumnNumberException;

	@Override
	public abstract void addCell(int colNumber, CellState color)
			throws CellColumnIsFullException, InvalidColumnNumberException;

	@Override
	public abstract GameState getGameState();

	protected BoardTemplate(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		initCellBoard();
		initBoardCols();
	}

	protected void initBoardCols() {
		boardCols = new HashMap<Integer, List<Cell>>();
		for (int x = 0; x < xSize; x++) {
			boardCols.put(x, new ArrayList<Cell>());
			for (int y = 0; y < ySize; y++) {
				boardCols.get(x).add(cellBoard[x][y]);
			}
		}

	}

	protected void initCellBoard() {
		cellBoard = new Cell[xSize][ySize];
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				cellBoard[x][y] = new Cell(CellState.EMPTY, x, y);
			}
		}
	}

	@Override
	public Map<Integer, List<Cell>> getBoardCols() {
		return boardCols;
	}

	@Override
	public List<Cell> cellBoardToList() {
		return Arrays.stream(cellBoard).flatMap(Arrays::stream).collect(Collectors.toList());
	}

}
