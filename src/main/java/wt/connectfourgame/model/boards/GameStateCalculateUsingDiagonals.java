package wt.connectfourgame.model.boards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import wt.connectfourgame.model.states.Cell;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;

@EqualsAndHashCode
public class GameStateCalculateUsingDiagonals implements GameStateCalculateStrategy, Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<List<Cell>> allBoardDiagonalCells;

	@Override
	public void prepare(BoardTemplate boardTemplate) {
		allBoardDiagonalCells = new ArrayList<List<Cell>>();
		addVerticalDiagonals(boardTemplate);
		addHorizontalDiagonals(boardTemplate);
		addLeftCrossDiagonals(boardTemplate);
		addRightCrossDiagonals(boardTemplate);
	}

	@Override
	public GameState calculate() {
		return calculateGameStateBasedOnCellsRepeats(allBoardDiagonalCells);
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

	private void addVerticalDiagonals(BoardTemplate boardTemplate) {
		boardTemplate.getBoardCols().forEach((key, value) -> allBoardDiagonalCells.add(value));
	}

	private void addHorizontalDiagonals(BoardTemplate boardTemplate) {
		for (int y = 0; y < boardTemplate.getySize(); y++) {
			List<Cell> cellList = new ArrayList<Cell>();
			for (int x = 0; x < boardTemplate.getxSize(); x++) {
				cellList.add(boardTemplate.getCellBoard()[x][y]);
			}
			allBoardDiagonalCells.add(cellList);
		}
	}

	private void addLeftCrossDiagonals(BoardTemplate boardTemplate) {
		for (int y = 0; y < boardTemplate.getySize(); y++) {
			List<Cell> cellList = new ArrayList<Cell>();
			int tempY = y;
			int tempX = 0;
			while (tempY < boardTemplate.getySize() && tempX < boardTemplate.getxSize()) {
				cellList.add(boardTemplate.getCellBoard()[tempX][tempY]);
				tempY++;
				tempX++;
			}
			allBoardDiagonalCells.add(cellList);
		}
	}

	private void addRightCrossDiagonals(BoardTemplate boardTemplate) {
		for (int y = 0; y < boardTemplate.getySize(); y++) {
			List<Cell> cellList = new ArrayList<Cell>();
			int tempY = y;
			int tempX = boardTemplate.getxSize() - 1;
			while (tempY < boardTemplate.getySize() && tempX < boardTemplate.getxSize()) {
				cellList.add(boardTemplate.getCellBoard()[tempX][tempY]);
				tempY++;
				tempX--;
			}
			allBoardDiagonalCells.add(cellList);
		}
	}

}
