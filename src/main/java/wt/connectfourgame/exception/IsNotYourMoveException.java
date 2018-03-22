package wt.connectfourgame.exception;

public class IsNotYourMoveException extends Exception{

	private static final long serialVersionUID = 1L;

	public IsNotYourMoveException() {
		super("Wait for your opponnent move. It is not your turn.");
	}
}
