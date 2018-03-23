package wt.connectfourgame.fixtures;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Optional;

import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.command.ResponseCode;
import wt.connectfourgame.command.ResponseStatusCommand;

public class CommandFixture {

	public RegisterCommand generateRegisterCommand(String nickname) {
		RegisterCommand registerCommand = new RegisterCommand();
		registerCommand.setNickname(nickname);
		return registerCommand;
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
