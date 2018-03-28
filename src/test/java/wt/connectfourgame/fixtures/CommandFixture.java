package wt.connectfourgame.fixtures;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Optional;

import wt.connectfourgame.command.PlayerMoveCommand;
import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.command.ResponseCode;
import wt.connectfourgame.command.ResponseStatusCommand;
import wt.connectfourgame.model.states.GameState;

public class CommandFixture {

	public RegisterCommand generateRegisterCommand(String nickname) {
		RegisterCommand registerCommand = new RegisterCommand();
		registerCommand.setNickname(nickname);
		return registerCommand;
	}

	public PlayerMoveCommand generatePlayerMoveCommand(String token, int colNumber) {
		return generatePlayerMoveCommandWithGameState(token, colNumber, null);
	}

	public PlayerMoveCommand generatePlayerMoveCommandWithGameState(String token, int colNumber, GameState gameState) {
		PlayerMoveCommand playerMoveCommand = new PlayerMoveCommand();
		playerMoveCommand.setToken(token);
		playerMoveCommand.setColNumber(colNumber);
		if (gameState != null)
			playerMoveCommand.setGameState(gameState);
		return playerMoveCommand;
	}

	public Optional<Entry<String, String>> generateMapEntry(String key, String value) {
		return Optional.of(new AbstractMap.SimpleEntry<String, String>(key, value));
	}

	public ResponseStatusCommand generateResponseStatusCommand(ResponseCode responseCode, String message) {
		ResponseStatusCommand responseStatusCommand = new ResponseStatusCommand();
		responseStatusCommand.setResponseCode(responseCode.name());
		responseStatusCommand.setMessage(message);
		return responseStatusCommand;
	}
}
