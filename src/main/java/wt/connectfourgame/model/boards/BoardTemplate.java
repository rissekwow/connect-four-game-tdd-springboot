package wt.connectfourgame.model.boards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
import wt.connectfourgame.model.states.Cell;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;

public abstract class BoardTemplate implements Board {

	private int xSize;
	private int ySize;

	private Cell[][] cellBoard;
	private Map<Integer, List<Cell>> boardCols;

	protected BoardTemplate(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
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
		List<List<Cell>> allBoardDiagonalCells = new ArrayList<List<Cell>>();
		addVerticalDiagonals(allBoardDiagonalCells);
		addHorizontalDiagonals(allBoardDiagonalCells);
		addLeftCrossDiagonals(allBoardDiagonalCells);
		addRightCrossDiagonals(allBoardDiagonalCells);
		return calculateGameStateBasedOnCellsRepeats(allBoardDiagonalCells);
	}

	@Override
	public Map<Integer, List<Cell>> getBoardCols() {
		return boardCols;
	}

	@Override
	public List<Cell> cellBoardToList() {
		return Arrays.stream(cellBoard).flatMap(Arrays::stream).collect(Collectors.toList());
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

	private GameState calculateGameStateBasedOnCellsRepeats(List<List<Cell>> allBoardDiagonalCells) {
		for (List<Cell> cellList : allBoardDiagonalCells) {
			int redRepeat = 0;
			int yellowRepeat = 0;
			CellState previousState = CellState.EMPTY;
			for (Cell cell : cellList) {
				CellState state = cell.getCellState();
				if (isGameStateChanged(previousState, state)) {
					previousState = state;
					redRepeat = 0;
					yellowRepeat = 0;
					if (state.equals(CellState.YELLOW))
						yellowRepeat++;
					if (state.equals(CellState.RED))
						redRepeat++;
				} else {
					if (state.equals(CellState.YELLOW))
						yellowRepeat++;
					if (state.equals(CellState.RED))
						redRepeat++;
					previousState = state;
				}
				if (yellowRepeat == 4)
					return GameState.YELLOW_WIN;
				if (redRepeat == 4)
					return GameState.RED_WIN;
			}
		}
		return GameState.OPEN;
	}

	private boolean isGameStateChanged(CellState previousState, CellState state) {
		return !state.equals(previousState);
	}

	private List<Cell> generateEmptyCellList() {
		List<Cell> emptyCells = new ArrayList<Cell>();
		boardCols.forEach((key, value) -> {
			emptyCells.addAll(
					value.stream().filter(v -> v.getCellState().equals(CellState.EMPTY)).collect(Collectors.toList()));
		});
		return emptyCells;
	}

	private void addVerticalDiagonals(List<List<Cell>> allBoardDiagonalCells) {
		boardCols.forEach((key, value) -> allBoardDiagonalCells.add(value));
	}

	private void addHorizontalDiagonals(List<List<Cell>> allBoardDiagonalCells) {
		for (int y = 0; y < ySize; y++) {
			List<Cell> cellList = new ArrayList<Cell>();
			for (int x = 0; x < xSize; x++) {
				cellList.add(cellBoard[x][y]);
			}
			allBoardDiagonalCells.add(cellList);
		}
	}

	private void addLeftCrossDiagonals(List<List<Cell>> allBoardDiagonalCells) {
		for (int y = 0; y < ySize; y++) {
			List<Cell> cellList = new ArrayList<Cell>();
			int tempY = y;
			int tempX = 0;
			while (tempY < ySize && tempX < xSize) {
				cellList.add(cellBoard[tempX][tempY]);
				tempY++;
				tempX++;
			}
			allBoardDiagonalCells.add(cellList);
		}
	}

	private void addRightCrossDiagonals(List<List<Cell>> allBoardDiagonalCells) {
		for (int y = 0; y < ySize; y++) {
			List<Cell> cellList = new ArrayList<Cell>();
			int tempY = y;
			int tempX = xSize - 1;
			while (tempY < ySize && tempX < xSize) {
				cellList.add(cellBoard[tempX][tempY]);
				tempY++;
				tempX--;
			}
			allBoardDiagonalCells.add(cellList);
		}
	}

}
