package wt.connectfourgame.controller;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import wt.connectfourgame.command.GameStartCommand;
import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.entity.GameEntity;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;
import wt.connectfourgame.manager.GameManager;
import wt.connectfourgame.model.states.CellState;

@Controller
public class WebSocketGameController {

	private final SimpMessageSendingOperations messagingTemplate;
	private final GameManager gameManager;
	private final String USER_NICKNAME_LISTENER = "/game/user/";
	private final String GAME_STARTED = "/start";
	private final String GAME_MOVE = "/move";
	private final String GAME_END = "/end";
	private final String GAME_LEAVE = "/leave";

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
			GameStartCommand commandToRed = new GameStartCommand();
			commandToRed.setYourColor(CellState.RED.name());
			commandToRed.setYourMove(true);

			GameStartCommand commandToYellow = new GameStartCommand();
			commandToRed.setYourColor(CellState.YELLOW.name());
			commandToRed.setYourMove(false);
			if (isRegisterPlayerRed) {
				gameManager.createGame(playerToken, opponent.get().getValue());
				messagingTemplate.convertAndSend(playerToken + GAME_STARTED, commandToRed);
				messagingTemplate.convertAndSend(opponent.get().getValue() + GAME_STARTED, commandToYellow);
			} else {
				gameManager.createGame(opponent.get().getValue(), playerToken);
				messagingTemplate.convertAndSend(playerToken + GAME_STARTED, commandToYellow);
				messagingTemplate.convertAndSend(opponent.get().getValue() + GAME_STARTED, commandToRed);
			}
		}
	}

	@MessageMapping("/move")
	public void processGameMove(RegisterCommand registerCommand) throws NicknameIsAlreadyInUseException {
		
	}

}
