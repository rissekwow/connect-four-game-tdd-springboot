package wt.connectfourgame.model.states;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Cell implements Serializable{

	private static final long serialVersionUID = 1L;
	
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
