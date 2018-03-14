package wt.connectfourgame.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

	private final int xSize = 9;
	private final int ySize = 9;

	private Cell[][] cellBoard;

	public Board() {
		initCellBoard();
	}

	private void initCellBoard() {
		cellBoard = new Cell[xSize][ySize];
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				cellBoard[x][y] = new Cell(CellState.EMPTY, x, y);
			}
		}
	}

	public List<Cell> getCellBoard() {
		return Arrays.stream(cellBoard).flatMap(Arrays::stream).collect(Collectors.toList());
	}

}
