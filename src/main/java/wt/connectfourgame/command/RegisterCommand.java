package wt.connectfourgame.command;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RegisterCommand {

	@NotNull
	private String nickname;
}
