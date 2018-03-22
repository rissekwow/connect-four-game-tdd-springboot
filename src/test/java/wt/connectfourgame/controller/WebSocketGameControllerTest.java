package wt.connectfourgame.controller;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit4.SpringRunner;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.command.PlayerMoveCommand;
import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.manager.GameManager;

@RunWith(SpringRunner.class)
public class WebSocketGameControllerTest implements WithAssertions, WithBDDMockito{

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
	private final String TEST_2_TOKEN = UUID.randomUUID().toString();
	
	@Before
	public void setup() {
		webSocketGameController = new WebSocketGameController(messagingTemplate, gameManager);
	}
	
	@Test
	public void registerUserWithFindOpponent() {
		RegisterCommand registerCommand = new RegisterCommand();
		registerCommand.setNickname(TEST_1_PLAYER);
		Map.Entry<String,String> entry =
			    new AbstractMap.SimpleEntry<String, String>(TEST_2_PLAYER,TEST_2_TOKEN);
		Optional<Entry<String,String>> optional = Optional.of(entry);
		doReturn(optional).when(gameManager).findOpponent();
		webSocketGameController.registerUserInQueue(registerCommand);
		verify(messagingTemplate, times(1)).convertAndSend("/game/user/"+TEST_1_PLAYER, TEST_1_PLAYER);
	}

}
