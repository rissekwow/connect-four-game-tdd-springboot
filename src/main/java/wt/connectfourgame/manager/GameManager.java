package wt.connectfourgame.manager;

import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.entity.GameEntity;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;
import wt.connectfourgame.model.boards.Board;
import wt.connectfourgame.model.boards.Board7x6;
import wt.connectfourgame.model.boards.GameStateCalculateUsingDiagonals;
import wt.connectfourgame.queue.OpponentQueue;
import wt.connectfourgame.repository.GameEntityRepository;
import wt.connectfourgame.serialize.BoardSerializator;

@Service
public class GameManager {

	private OpponentQueue opponentQueue;
	private BoardSerializator boardSerializator;
	private GameEntityRepository gameEntityRepository;

	@Autowired
	public GameManager(OpponentQueue opponentQueue, BoardSerializator boardSerializator,
			GameEntityRepository gameEntityRepository) {
		this.opponentQueue = opponentQueue;
		this.boardSerializator = boardSerializator;
		this.gameEntityRepository = gameEntityRepository;
	}

	public Optional<Entry<String, String>> findOpponent() {
		return opponentQueue.findOpponent();
	}

	public Entry<String, String> registerNickname(RegisterCommand registerCommand)
			throws NicknameIsAlreadyInUseException {
		return opponentQueue.registerNickname(registerCommand.getNickname());
	}

	public GameEntity createGame(String token1, String token2) {
		Board board = new Board7x6(new GameStateCalculateUsingDiagonals());
		GameEntity gameEntity = new GameEntity();
		gameEntity.setPlayerRedToken(token1);
		gameEntity.setPlayerYellowToken(token2);
		gameEntity.setRedMove(true);
		gameEntity.setGameState(board.getGameState());
		gameEntity.setSerializedBoard(boardSerializator.serializeBoardToBytes(board));
		return gameEntityRepository.save(gameEntity);
	}

}
