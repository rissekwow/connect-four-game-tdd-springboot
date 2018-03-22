package wt.connectfourgame.exception;

public class GameNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public GameNotExistException() {
		super("Your token is not valid in any game.");
	}
}
