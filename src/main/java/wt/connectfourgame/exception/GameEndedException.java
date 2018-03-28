package wt.connectfourgame.exception;

import wt.connectfourgame.model.states.GameState;

public class GameEndedException extends Exception {

	private static final long serialVersionUID = 1L;

	public GameEndedException(GameState state) {
		super("Game ended with state" + state.name()+ ".");
	}

}
