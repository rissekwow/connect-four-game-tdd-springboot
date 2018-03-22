package wt.connectfourgame.command;

import lombok.Data;

@Data
public class PlayerMoveCommand {

	private String token;
	private int colNumber;
	private String gameState;
}
