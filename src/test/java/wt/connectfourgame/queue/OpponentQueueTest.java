package wt.connectfourgame.queue;

import java.util.Map;

import org.assertj.core.api.WithAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;

public class OpponentQueueTest implements WithAssertions, WithBDDMockito {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private Map<String, String> nicknameMap;
	
	@Mock
	private Map<String, String> nicknameWaitingMap;

	@InjectMocks
	private OpponentQueue opponentQueue;

	private String TEST_NICKNAME = "test";
	

	@Test
	public void testFindOpponent() throws NicknameIsAlreadyInUseException {
		OpponentQueue opponentQueueTest = new OpponentQueue();
		opponentQueueTest.registerNickname(TEST_NICKNAME, TEST_NICKNAME);
		opponentQueueTest.addNicknameToWaitingQueue(TEST_NICKNAME);
		assertThat(opponentQueueTest.findOpponent().get().getKey()).isEqualTo(TEST_NICKNAME);
	}

	@Test
	public void registerNicknameReturnEntry() throws NicknameIsAlreadyInUseException {
		OpponentQueue opponentQueueTest = new OpponentQueue();
		opponentQueueTest.registerNickname(TEST_NICKNAME, TEST_NICKNAME);
		assertThat(opponentQueueTest.getNicknameToken(TEST_NICKNAME)).isEqualTo(TEST_NICKNAME);
	}

	@Test
	public void isNicknameExistThrowNicknameIsAlreadyInUseException() {
		when(nicknameMap.containsKey(TEST_NICKNAME)).thenReturn(true);
		assertThatThrownBy(() -> {
			opponentQueue.registerNickname(TEST_NICKNAME, TEST_NICKNAME);
		}).isInstanceOf(NicknameIsAlreadyInUseException.class)
				.hasMessageContaining("Nickname [" + TEST_NICKNAME + "] already exist in queue.");
	}

}
