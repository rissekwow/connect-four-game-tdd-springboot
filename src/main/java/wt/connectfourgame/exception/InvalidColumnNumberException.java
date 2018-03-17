package wt.connectfourgame.exception;

public class InvalidColumnNumberException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidColumnNumberException(int number) {
		super("The column number [" + number + "] does not exist.");
	}
}
