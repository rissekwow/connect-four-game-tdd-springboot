package wt.connectfourgame.controller;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.entity.GameEntity;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;
import wt.connectfourgame.manager.GameManager;

@Controller
public class WebSocketGameController {

	private final SimpMessageSendingOperations messagingTemplate;
	private final GameManager gameManager;
	private final String USER_NICKNAME_LISTENER = "/game/user/";

	@Autowired
	public WebSocketGameController(SimpMessageSendingOperations messagingTemplate, GameManager gameManager) {
		this.messagingTemplate = messagingTemplate;
		this.gameManager = gameManager;
	}

	@MessageMapping("/register")
	public void registerUserInQueue(RegisterCommand registerCommand) throws NicknameIsAlreadyInUseException {
		Optional<Entry<String, String>> opponent = gameManager.findOpponent();
		if (!opponent.isPresent()) {
			Entry<String, String> userEntry = gameManager.registerNickname(registerCommand);
			messagingTemplate.convertAndSend(USER_NICKNAME_LISTENER + userEntry.getKey(), userEntry.getValue());
		} else {
			String playerToken = UUID.randomUUID().toString();
			messagingTemplate.convertAndSend(USER_NICKNAME_LISTENER + registerCommand.getNickname(), playerToken);
			boolean isRegisterPlayerRed = Math.round(Math.random()) == 0;
			GameEntity gameEntity = isRegisterPlayerRed ? gameManager.createGame(playerToken, opponent.get().getValue())
					: gameManager.createGame(opponent.get().getValue(), playerToken);

		}
	}

}
