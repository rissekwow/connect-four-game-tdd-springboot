package wt.connectfourgame.model.states;

public class Cell {

	private CellState cellState;
	private int x;
	private int y;

	public Cell(CellState cellState, int x, int y) {
		this.cellState = cellState;
		this.x = x;
		this.y = y;
	}

	public CellState getCellState() {
		return cellState;
	}

	public void setCellState(CellState cellState) {
		this.cellState = cellState;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
