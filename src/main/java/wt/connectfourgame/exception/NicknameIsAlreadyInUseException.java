package wt.connectfourgame.exception;

public class NicknameIsAlreadyInUseException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public NicknameIsAlreadyInUseException(String nickname) {
		super("Nickname ["+nickname+"] already exist in queue.");
	}

}
