package wt.connectfourgame.controller;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.command.ResponseCode;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;
import wt.connectfourgame.fixtures.CommandFixture;
import wt.connectfourgame.manager.GameManager;
import wt.connectfourgame.model.states.CellState;

public class WebSocketGameControllerTest implements WithAssertions, WithBDDMockito {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private GameManager gameManager;

	@Mock
	private SimpMessageSendingOperations messagingTemplate;

	private WebSocketGameController webSocketGameController;

	private final String TEST_1_PLAYER = "TESTER";
	private final String TEST_2_PLAYER = "USER";
	private final String TEST_1_TOKEN = UUID.randomUUID().toString();
	private final String TOKEN_MESSAGE = "/game/token/";
	private final String USER_NICKNAME_LISTENER = "/game/user/";

	@Before
	public void setup() {
		webSocketGameController = new WebSocketGameController(messagingTemplate, gameManager);
	}

	@Test
	public void registerWhenPlayersNotExistAndVerifyIfPlayerIsAddedToWaitingQueue()
			throws NicknameIsAlreadyInUseException {
		CommandFixture commandFixture = new CommandFixture();
		RegisterCommand playerWhoIsRequestingForRegistration = commandFixture.generateRegisterCommand(TEST_1_PLAYER);

		webSocketGameController.registerUserInQueue(playerWhoIsRequestingForRegistration);

		verify(messagingTemplate, times(1)).convertAndSend(USER_NICKNAME_LISTENER + TEST_1_PLAYER,
				commandFixture.generateResponseStatusCommand(ResponseCode.TOKEN_REGISTERED,
						gameManager.getNicknameToken(TEST_1_PLAYER)));
	}

	@Test
	public void registerWhenPlayerExistWithThrowNicknameAlreadyExistException() throws NicknameIsAlreadyInUseException {
		CommandFixture commandFixture = new CommandFixture();
		RegisterCommand playerWhoIsRequestingForRegistration = commandFixture.generateRegisterCommand(TEST_1_PLAYER);
		
		doThrow(new NicknameIsAlreadyInUseException(playerWhoIsRequestingForRegistration.getNickname()))
				.when(gameManager).registerNickname(playerWhoIsRequestingForRegistration);
		
		webSocketGameController.registerUserInQueue(playerWhoIsRequestingForRegistration);
		
		verify(messagingTemplate, times(1)).convertAndSend(USER_NICKNAME_LISTENER + TEST_1_PLAYER,
				commandFixture.generateResponseStatusCommand(ResponseCode.ERROR, "Nickname ["
						+ playerWhoIsRequestingForRegistration.getNickname() + "] already exist in queue."));
	}

	@Test
	public void registerUserWhenPlayerAlreadyExistAndRequestingPlayerIsRed() {
		CommandFixture commandFixture = new CommandFixture();
		RegisterCommand playerWhoIsRequestingForRegistration = commandFixture.generateRegisterCommand(TEST_2_PLAYER);
		Optional<Entry<String, String>> existedPlayerOptionalNicknameAndToken = commandFixture
				.generateMapEntry(TEST_1_PLAYER, TEST_1_TOKEN);

		when(gameManager.findOpponent()).thenReturn(existedPlayerOptionalNicknameAndToken);
		when(gameManager.getNicknameToken(TEST_1_PLAYER)).thenReturn(TEST_1_TOKEN);
		when(gameManager.areYouRedPlayer()).thenReturn(true);

		webSocketGameController.registerUserInQueue(playerWhoIsRequestingForRegistration);

		verify(messagingTemplate, times(1)).convertAndSend(USER_NICKNAME_LISTENER + TEST_2_PLAYER,
				commandFixture.generateResponseStatusCommand(ResponseCode.TOKEN_REGISTERED, gameManager.getNicknameToken(TEST_2_PLAYER)));
		verify(messagingTemplate, times(1)).convertAndSend(TOKEN_MESSAGE + gameManager.getNicknameToken(TEST_2_PLAYER),
				commandFixture.generateResponseStatusCommand(ResponseCode.GAME_STARTED, CellState.RED.name()));
		verify(messagingTemplate, times(1)).convertAndSend(TOKEN_MESSAGE + gameManager.getNicknameToken(TEST_1_PLAYER),
				commandFixture.generateResponseStatusCommand(ResponseCode.GAME_STARTED, CellState.YELLOW.name()));
	}

	@Test
	public void registerUserWhenOnePlayerAlreadyExistAndRequestingPlayerIsYellow() {
		CommandFixture commandFixture = new CommandFixture();
		RegisterCommand playerWhoIsRequestingForRegistration = commandFixture.generateRegisterCommand(TEST_2_PLAYER);
		Optional<Entry<String, String>> existedPlayerOptionalNicknameAndToken = commandFixture
				.generateMapEntry(TEST_1_PLAYER, TEST_1_TOKEN);

		when(gameManager.findOpponent()).thenReturn(existedPlayerOptionalNicknameAndToken);
		when(gameManager.getNicknameToken(TEST_1_PLAYER)).thenReturn(TEST_1_TOKEN);
		when(gameManager.areYouRedPlayer()).thenReturn(false);

		webSocketGameController.registerUserInQueue(playerWhoIsRequestingForRegistration);

		verify(messagingTemplate, times(1)).convertAndSend(USER_NICKNAME_LISTENER + TEST_2_PLAYER,
				commandFixture.generateResponseStatusCommand(ResponseCode.TOKEN_REGISTERED, gameManager.getNicknameToken(TEST_2_PLAYER)));
		verify(messagingTemplate, times(1)).convertAndSend(TOKEN_MESSAGE + gameManager.getNicknameToken(TEST_2_PLAYER),
				commandFixture.generateResponseStatusCommand(ResponseCode.GAME_STARTED, CellState.YELLOW.name()));
		verify(messagingTemplate, times(1)).convertAndSend(TOKEN_MESSAGE + gameManager.getNicknameToken(TEST_1_PLAYER),
				commandFixture.generateResponseStatusCommand(ResponseCode.GAME_STARTED, CellState.RED.name()));
	}
	
	

}
