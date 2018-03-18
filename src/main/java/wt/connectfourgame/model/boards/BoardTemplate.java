package wt.connectfourgame.model.boards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
import wt.connectfourgame.model.states.Cell;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;

@EqualsAndHashCode
public abstract class BoardTemplate implements Board, Serializable {

	private static final long serialVersionUID = 1L;
	private int xSize;
	private int ySize;

	private Cell[][] cellBoard;
	private Map<Integer, List<Cell>> boardCols;
	private GameStateCalculateStrategy gameStateCalculateStrategy;

	protected BoardTemplate(int xSize, int ySize, GameStateCalculateStrategy gameStateCalculateStrategy) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.gameStateCalculateStrategy = gameStateCalculateStrategy;
		initCellBoard();
		initBoardCols();
	}

	@Override
	public boolean isAddToColAvailable(int colNumber) throws InvalidColumnNumberException {
		if (isColNumberValid(colNumber))
			throw new InvalidColumnNumberException(colNumber);
		
		return boardCols.get(Integer.valueOf(colNumber)).stream().reduce((first, second) -> second).get().getCellState()
				.equals(CellState.EMPTY);
	}

	@Override
	public void addCell(int colNumber, CellState color) throws CellColumnIsFullException, InvalidColumnNumberException {
		if (!isAddToColAvailable(colNumber))
			throw new CellColumnIsFullException();
		Optional<Cell> cell = boardCols.get(colNumber).stream().filter(c -> c.getCellState().equals(CellState.EMPTY))
				.findFirst();
		cell.ifPresent(c -> c.setCellState(color));
	}

	@Override
	public GameState getGameState() {
		if (generateEmptyCellList().isEmpty())
			return GameState.DRAW;
		gameStateCalculateStrategy.prepare(this);
		return gameStateCalculateStrategy.calculate();
	}

	@Override
	public Map<Integer, List<Cell>> getBoardCols() {
		return boardCols;
	}

	@Override
	public List<Cell> cellBoardToList() {
		return Arrays.stream(cellBoard).flatMap(Arrays::stream).collect(Collectors.toList());
	}

	public int getxSize() {
		return xSize;
	}

	public int getySize() {
		return ySize;
	}

	public Cell[][] getCellBoard() {
		return cellBoard;
	}

	private boolean isColNumberValid(int colNumber) {
		return colNumber < 0 || colNumber >= xSize;
	}

	private void initBoardCols() {
		boardCols = new HashMap<Integer, List<Cell>>();
		for (int x = 0; x < xSize; x++) {
			boardCols.put(x, new ArrayList<Cell>());
			for (int y = 0; y < ySize; y++) {
				boardCols.get(x).add(cellBoard[x][y]);
			}
		}
	}

	private void initCellBoard() {
		cellBoard = new Cell[xSize][ySize];
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				cellBoard[x][y] = new Cell(CellState.EMPTY, x, y);
			}
		}
	}

	private List<Cell> generateEmptyCellList() {
		List<Cell> emptyCells = new ArrayList<Cell>();
		boardCols.forEach((key, value) -> {
			emptyCells.addAll(
					value.stream().filter(v -> v.getCellState().equals(CellState.EMPTY)).collect(Collectors.toList()));
		});
		return emptyCells;
	}

}
