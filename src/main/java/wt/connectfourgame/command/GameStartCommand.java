package wt.connectfourgame.command;

import lombok.Data;

@Data
public class GameStartCommand {

	private String yourColor;
	private boolean isYourMove;
}
