package wt.connectfourgame.exception;

import java.io.Serializable;

public class InvalidColumnNumberException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;

	public InvalidColumnNumberException(int number) {
		super("The column number [" + number + "] does not exist.");
	}
}
