package wt.connectfourgame.controller;

import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import wt.connectfourgame.command.PlayerMoveCommand;
import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.command.ResponseCode;
import wt.connectfourgame.command.ResponseStatusCommand;
import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.GameNotExistException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
import wt.connectfourgame.exception.IsNotYourMoveException;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;
import wt.connectfourgame.manager.GameManager;
import wt.connectfourgame.model.states.CellState;

@Controller
public class WebSocketGameController {

	private final SimpMessageSendingOperations messagingTemplate;
	private final GameManager gameManager;
	private final String USER_NICKNAME_LISTENER = "/game/user/";
	private final String TOKEN_MESSAGE = "/game/token/";

	@Autowired
	public WebSocketGameController(SimpMessageSendingOperations messagingTemplate, GameManager gameManager) {
		this.messagingTemplate = messagingTemplate;
		this.gameManager = gameManager;
	}

	@MessageMapping("/register")
	public void registerUserInQueue(RegisterCommand registerCommand) {
		registerNicknameAndSendTokenOrErrorResponse(registerCommand);
		Optional<Entry<String, String>> opponent = gameManager.findOpponent();
		if (!opponent.isPresent())
			gameManager.addNicknameToWaitingQueue(registerCommand.getNickname());
		opponent.ifPresent(o -> createNewGameAndSendMessagesToPlayers(registerCommand, opponent));
	}
	
	@MessageMapping("/move")
	public void processGameMove(PlayerMoveCommand playerMoveCommand) {
		try {
			PlayerMoveCommand playerMoveCommandResponse = gameManager.progressMove(playerMoveCommand);
			messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommandResponse.getToken(), new Object());
		} catch (GameNotExistException | IsNotYourMoveException | CellColumnIsFullException
				| InvalidColumnNumberException e) {
			messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommand.getToken(),
					generateResponseCommand(ResponseCode.ERROR, e.getMessage()));
		}
	}

	private void createNewGameAndSendMessagesToPlayers(RegisterCommand registerCommand,
			Optional<Entry<String, String>> opponent) {
		String playerToken = gameManager.getNicknameToken(registerCommand.getNickname());
		boolean isRegisterPlayerRed = gameManager.areYouRedPlayer();
		if (isRegisterPlayerRed)
			createGameAndSendStartMessageWhenRequesterPlayerIsRed(opponent, playerToken);
		else
			createGameAndSendStartMessageWhenRequesterPlayerIsYellow(opponent, playerToken);
	}

	private void registerNicknameAndSendTokenOrErrorResponse(RegisterCommand registerCommand) {
		try {
			gameManager.registerNickname(registerCommand);
			String nicknameToken = gameManager.getNicknameToken(registerCommand.getNickname());
			messagingTemplate.convertAndSend(USER_NICKNAME_LISTENER + registerCommand.getNickname(),
					generateResponseCommand(ResponseCode.TOKEN_REGISTERED, nicknameToken));
		} catch (NicknameIsAlreadyInUseException e) {
			messagingTemplate.convertAndSend(USER_NICKNAME_LISTENER + registerCommand.getNickname(),
					generateResponseCommand(ResponseCode.ERROR, e.getMessage()));
		}
	}

	private void createGameAndSendStartMessageWhenRequesterPlayerIsYellow(Optional<Entry<String, String>> opponent,
			String playerToken) {
		gameManager.createGame(opponent.get().getValue(), playerToken);
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerToken,
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.YELLOW.name()));
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + opponent.get().getValue(),
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.RED.name()));
	}

	private void createGameAndSendStartMessageWhenRequesterPlayerIsRed(Optional<Entry<String, String>> opponent,
			String playerToken) {
		gameManager.createGame(playerToken, opponent.get().getValue());
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerToken,
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.RED.name()));
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + opponent.get().getValue(),
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.YELLOW.name()));
	}

	private ResponseStatusCommand generateResponseCommand(ResponseCode responseCode, String message) {
		ResponseStatusCommand responseStatusCommand = new ResponseStatusCommand();
		responseStatusCommand.setResponseCode(responseCode.name());
		responseStatusCommand.setMessage(message);
		return responseStatusCommand;
	}

}
