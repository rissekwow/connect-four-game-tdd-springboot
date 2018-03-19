package wt.connectfourgame.queue;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
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

	@InjectMocks
	private OpponentQueue opponentQueue;

	private String TEST_NICKNAME = "test";

	@Test
	public void testFindOpponent() throws NicknameIsAlreadyInUseException {
		OpponentQueue opponentQueueTest = new OpponentQueue();
		opponentQueueTest.registerNickname(TEST_NICKNAME);
		assertThat(opponentQueueTest.findOpponent().get().getKey()).isEqualTo(TEST_NICKNAME);
	}

	@Test
	public void registerNicknameReturnEntry() throws NicknameIsAlreadyInUseException {
		OpponentQueue opponentQueueTest = new OpponentQueue();
		Entry<String, String> entry = opponentQueueTest.registerNickname(TEST_NICKNAME);
		assertThat(entry.getKey()).isEqualTo(TEST_NICKNAME);
	}

	@Test
	public void isNicknameExistThrowNicknameIsAlreadyInUseException() {
		when(nicknameMap.containsKey(TEST_NICKNAME)).thenReturn(true);
		assertThatThrownBy(() -> {
			opponentQueue.registerNickname(TEST_NICKNAME);
		}).isInstanceOf(NicknameIsAlreadyInUseException.class)
				.hasMessageContaining("Nickname [" + TEST_NICKNAME + "] already exist in queue.");
	}

}
