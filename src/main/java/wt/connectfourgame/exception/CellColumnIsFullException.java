package wt.connectfourgame.exception;

public class CellColumnIsFullException extends Exception {

	private static final long serialVersionUID = 1L;

	public CellColumnIsFullException() {
		super("Cell columns is filled with unempty cells.");
	}
}
