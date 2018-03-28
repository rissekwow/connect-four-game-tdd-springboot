package wt.connectfourgame.manager;

import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.command.PlayerMoveCommand;
import wt.connectfourgame.entity.GameEntity;
import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.GameEndedException;
import wt.connectfourgame.exception.GameNotExistException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
import wt.connectfourgame.exception.IsNotYourMoveException;
import wt.connectfourgame.model.boards.Board;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;
import wt.connectfourgame.repository.GameEntityRepository;
import wt.connectfourgame.serialize.BoardSerializator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameManagerIntegrationTest implements WithAssertions, WithBDDMockito {

	private final String TOKEN_RED_TEST = UUID.randomUUID().toString();
	private final String TOKEN_YELLOW_TEST = UUID.randomUUID().toString();

	@Autowired
	private GameManager gameManager;

	@Autowired
	private GameEntityRepository gameEntityRepository;

	@Autowired
	private BoardSerializator boardSerializator;

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

	@Test
	public void progressMove() throws GameNotExistException, IsNotYourMoveException, CellColumnIsFullException,
			InvalidColumnNumberException, GameEndedException {
		int colNumber = 0;
		gameManager.createGame(TOKEN_RED_TEST, TOKEN_YELLOW_TEST);
		PlayerMoveCommand playerMoveCommand = new PlayerMoveCommand();
		playerMoveCommand.setToken(TOKEN_RED_TEST);
		playerMoveCommand.setColNumber(colNumber);
		PlayerMoveCommand playerMoveCommandResponse = gameManager.progressMove(playerMoveCommand);
		Optional<GameEntity> afterMoveEntity = gameEntityRepository.findByPlayerRedToken(TOKEN_RED_TEST);
		assertThat(afterMoveEntity).isPresent();
		GameEntity gameEntity = afterMoveEntity.get();
		Board board = boardSerializator.deserializeBytesToBoard(gameEntity.getSerializedBoard());
		assertThat(board.getBoardCols().get(colNumber).get(0).getCellState()).isEqualTo(CellState.RED);
		assertThat(gameEntity.isRedMove()).isFalse();
		assertThat(playerMoveCommandResponse.getColNumber()).isEqualTo(colNumber);
		assertThat(playerMoveCommandResponse.getGameState()).isEqualTo(GameState.OPEN);
		assertThat(playerMoveCommandResponse.getToken()).isEqualTo(TOKEN_YELLOW_TEST);
	}

}
