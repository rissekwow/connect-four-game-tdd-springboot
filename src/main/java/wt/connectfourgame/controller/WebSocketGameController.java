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
import wt.connectfourgame.exception.GameEndedException;
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
		opponent.ifPresent(o -> createNewGameAndSendMessagesToPlayers(registerCommand, o));
	}
	
	@MessageMapping("/disconnect")
	public void registerDisconnect(RegisterCommand registerCommand) {
		registerNicknameAndSendTokenOrErrorResponse(registerCommand);
		Optional<Entry<String, String>> opponent = gameManager.findOpponent();
		if (!opponent.isPresent())
			gameManager.addNicknameToWaitingQueue(registerCommand.getNickname());
		opponent.ifPresent(o -> createNewGameAndSendMessagesToPlayers(registerCommand, o));
	}

	@MessageMapping("/move")
	public void processGameMove(PlayerMoveCommand playerMoveCommand) {
		try {
			PlayerMoveCommand playerMoveCommandResponse = gameManager.progressMove(playerMoveCommand);
			switch (playerMoveCommandResponse.getGameState()) {
			case OPEN:
				sendMoveResponseToOpponentPlayer(playerMoveCommandResponse);
				break;
			case DRAW:
				sendDrawResponseToBothPlayers(playerMoveCommand, playerMoveCommandResponse);
				break;
			case RED_WIN:
				sendPlayerRedWinToBothPlayers(playerMoveCommand, playerMoveCommandResponse);
				break;
			case YELLOW_WIN:
				sendYellowPlayerWinToBothPlayers(playerMoveCommand, playerMoveCommandResponse);
				break;
			default:
				throw new RuntimeException("Bug! Invalid GameState.");
			}

		} catch (GameNotExistException | IsNotYourMoveException | CellColumnIsFullException
				| InvalidColumnNumberException | GameEndedException e) {
			sendErrorResponseToCurrentPlayer(playerMoveCommand.getToken(), e);
		}
	}

	private void sendErrorResponseToCurrentPlayer(String playerMoveToken, Exception e) {
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveToken,
				generateResponseCommand(ResponseCode.ERROR, e.getMessage()));
	}

	private void sendYellowPlayerWinToBothPlayers(PlayerMoveCommand playerMoveCommand,
			PlayerMoveCommand playerMoveCommandResponse) {
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommand.getToken(),
				generateResponseCommand(ResponseCode.YELLOW_WIN, ResponseCode.YELLOW_WIN.name()));
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommandResponse.getToken(),
				generateResponseCommand(ResponseCode.YELLOW_WIN, ResponseCode.YELLOW_WIN.name()));
	}

	private void sendPlayerRedWinToBothPlayers(PlayerMoveCommand playerMoveCommand,
			PlayerMoveCommand playerMoveCommandResponse) {
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommand.getToken(),
				generateResponseCommand(ResponseCode.RED_WIN, ResponseCode.RED_WIN.name()));
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommandResponse.getToken(),
				generateResponseCommand(ResponseCode.RED_WIN, ResponseCode.RED_WIN.name()));
	}

	private void sendDrawResponseToBothPlayers(PlayerMoveCommand playerMoveCommand,
			PlayerMoveCommand playerMoveCommandResponse) {
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommand.getToken(),
				generateResponseCommand(ResponseCode.DRAW, ResponseCode.DRAW.name()));
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommandResponse.getToken(),
				generateResponseCommand(ResponseCode.DRAW, ResponseCode.DRAW.name()));
	}

	private void sendMoveResponseToOpponentPlayer(PlayerMoveCommand playerMoveCommandResponse) {
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerMoveCommandResponse.getToken(),
				generateResponseCommand(ResponseCode.OPPONENT_MOVE, playerMoveCommandResponse.getColNumber()));
	}

	private void createNewGameAndSendMessagesToPlayers(RegisterCommand registerCommand,
			Entry<String, String> opponent) {
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

	private void createGameAndSendStartMessageWhenRequesterPlayerIsYellow(Entry<String, String> opponent,
			String playerToken) {
		gameManager.createGame(opponent.getValue(), playerToken);
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerToken,
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.YELLOW.name()));
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + opponent.getValue(),
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.RED.name()));
	}

	private void createGameAndSendStartMessageWhenRequesterPlayerIsRed(Entry<String, String> opponent,
			String playerToken) {
		gameManager.createGame(playerToken, opponent.getValue());
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + playerToken,
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.RED.name()));
		messagingTemplate.convertAndSend(TOKEN_MESSAGE + opponent.getValue(),
				generateResponseCommand(ResponseCode.GAME_STARTED, CellState.YELLOW.name()));
	}

	private ResponseStatusCommand generateResponseCommand(ResponseCode responseCode, int message) {
		return generateResponseCommand(responseCode, String.valueOf(message));
	}

	private ResponseStatusCommand generateResponseCommand(ResponseCode responseCode, String message) {
		ResponseStatusCommand responseStatusCommand = new ResponseStatusCommand();
		responseStatusCommand.setResponseCode(responseCode.name());
		responseStatusCommand.setMessage(message);
		return responseStatusCommand;
	}

}
