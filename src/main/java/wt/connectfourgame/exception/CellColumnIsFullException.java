package wt.connectfourgame.exception;

import java.io.Serializable;

public class CellColumnIsFullException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;

	public CellColumnIsFullException() {
		super("Cell columns is filled with unempty cells.");
	}
}
