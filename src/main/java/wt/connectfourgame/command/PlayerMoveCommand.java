package wt.connectfourgame.command;

import lombok.Data;
import wt.connectfourgame.model.states.GameState;

@Data
public class PlayerMoveCommand {

	private String token;
	private int colNumber;
	private GameState gameState;
}
