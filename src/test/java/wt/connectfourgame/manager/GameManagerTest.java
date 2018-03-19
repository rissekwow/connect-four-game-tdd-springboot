package wt.connectfourgame.manager;

import java.util.UUID;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.repository.GameEntityRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameManagerTest implements WithAssertions, WithBDDMockito {

	@Autowired
	private GameManager gameManager;

	@Autowired
	private GameEntityRepository gameEntityRepository;

	@Test
	public void createGameSuccessfull() {
		String token1 = UUID.randomUUID().toString();
		String token2 = UUID.randomUUID().toString();
		gameManager.createGame(token1, token2);
		assertThat(gameEntityRepository.findByPlayerRedToken(token1)).isPresent();
		assertThat(gameEntityRepository.findByPlayerYellowToken(token2)).isPresent();
		assertThat(gameEntityRepository.findByPlayerRedToken(token2)).isNotPresent();
		assertThat(gameEntityRepository.findByPlayerYellowToken(token1)).isNotPresent();
	}

}
