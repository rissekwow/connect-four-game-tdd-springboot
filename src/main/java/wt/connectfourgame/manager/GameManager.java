package wt.connectfourgame.manager;

import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wt.connectfourgame.command.PlayerMoveCommand;
import wt.connectfourgame.command.RegisterCommand;
import wt.connectfourgame.entity.GameEntity;
import wt.connectfourgame.exception.CellColumnIsFullException;
import wt.connectfourgame.exception.GameEndedException;
import wt.connectfourgame.exception.GameNotExistException;
import wt.connectfourgame.exception.InvalidColumnNumberException;
import wt.connectfourgame.exception.IsNotYourMoveException;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;
import wt.connectfourgame.generator.TokenGenerator;
import wt.connectfourgame.model.boards.Board;
import wt.connectfourgame.model.boards.Board7x6;
import wt.connectfourgame.model.boards.GameStateCalculateUsingDiagonals;
import wt.connectfourgame.model.states.CellState;
import wt.connectfourgame.model.states.GameState;
import wt.connectfourgame.queue.OpponentQueue;
import wt.connectfourgame.repository.GameEntityRepository;
import wt.connectfourgame.serialize.BoardSerializator;

@Service
public class GameManager {

	private OpponentQueue opponentQueue;
	private BoardSerializator boardSerializator;
	private GameEntityRepository gameEntityRepository;
	private TokenGenerator tokenGenerator;

	@Autowired
	public GameManager(OpponentQueue opponentQueue, BoardSerializator boardSerializator,
			GameEntityRepository gameEntityRepository, TokenGenerator tokenGenerator) {
		this.opponentQueue = opponentQueue;
		this.boardSerializator = boardSerializator;
		this.gameEntityRepository = gameEntityRepository;
		this.tokenGenerator = tokenGenerator;
	}

	public void createGame(String token1, String token2) {
		Board board = new Board7x6(new GameStateCalculateUsingDiagonals());
		GameEntity gameEntity = new GameEntity();
		gameEntity.setPlayerRedToken(token1);
		gameEntity.setPlayerYellowToken(token2);
		gameEntity.setRedMove(true);
		gameEntity.setGameState(board.getGameState());
		gameEntity.setSerializedBoard(boardSerializator.serializeBoardToBytes(board));
		gameEntityRepository.save(gameEntity);
	}

	public PlayerMoveCommand progressMove(PlayerMoveCommand playerMoveCommand) throws GameNotExistException,
			IsNotYourMoveException, CellColumnIsFullException, InvalidColumnNumberException, GameEndedException {
		Optional<GameEntity> gameEntity = gameEntityRepository.findByPlayerRedToken(playerMoveCommand.getToken());
		boolean isRedPlayer = gameEntity.isPresent();
		gameEntity = findPlayerByYellowTokenIfRedTokenNotExist(playerMoveCommand, gameEntity);
		throwGameNotExistExceptionWhenTokensWasntFound(gameEntity);
		GameEntity entity = gameEntity.get();
		throwIsNotYourMoveExceptionWhenIsNotYourTurn(isRedPlayer, entity);
		throwGameEndedExceptionWhenGameIsntOpen(entity);
		Board board = boardSerializator.deserializeBytesToBoard(entity.getSerializedBoard());
		GameState gameState = makeMoveAndSaveBoard(playerMoveCommand, isRedPlayer, entity, board);
		removeNicknamesWhenGameIsFinished(entity);
		PlayerMoveCommand playerMoveCommandResponse = generatePlayerMoveCommand(playerMoveCommand, isRedPlayer, entity,
				gameState);
		return playerMoveCommandResponse;
	}

	public Optional<Entry<String, String>> findOpponent() {
		return opponentQueue.findOpponent();
	}

	public boolean areYouRedPlayer() {
		return Math.round(Math.random()) == 0;
	}

	public void addNicknameToWaitingQueue(String nickname) {
		opponentQueue.addNicknameToWaitingQueue(nickname);
	}

	public void registerNickname(RegisterCommand registerCommand) throws NicknameIsAlreadyInUseException {
		opponentQueue.registerNickname(registerCommand.getNickname(), tokenGenerator.generateToken());
	}

	public String getNicknameToken(String nickname) {
		return opponentQueue.getNicknameToken(nickname);
	}

	private PlayerMoveCommand generatePlayerMoveCommand(PlayerMoveCommand playerMoveCommand, boolean isRedPlayer,
			GameEntity entity, GameState gameState) {
		PlayerMoveCommand playerMoveCommandResponse = new PlayerMoveCommand();
		playerMoveCommandResponse.setColNumber(playerMoveCommand.getColNumber());
		playerMoveCommandResponse.setToken(isRedPlayer ? entity.getPlayerYellowToken() : entity.getPlayerRedToken());
		playerMoveCommandResponse.setGameState(gameState);
		return playerMoveCommandResponse;
	}

	private void removeNicknamesWhenGameIsFinished(GameEntity entity) {
		if (!entity.getGameState().equals(GameState.OPEN)) {
			opponentQueue.removeNicknameByToken(entity.getPlayerRedToken());
			opponentQueue.removeNicknameByToken(entity.getPlayerYellowToken());
		}
	}

	private GameState makeMoveAndSaveBoard(PlayerMoveCommand playerMoveCommand, boolean isRedPlayer, GameEntity entity,
			Board board) throws CellColumnIsFullException, InvalidColumnNumberException {
		board.addCell(playerMoveCommand.getColNumber(), isRedPlayer ? CellState.RED : CellState.YELLOW);
		GameState gameState = board.getGameState();
		entity.setGameState(gameState);
		entity.setRedMove(!isRedPlayer);
		entity.setSerializedBoard(boardSerializator.serializeBoardToBytes(board));
		gameEntityRepository.save(entity);
		return gameState;
	}

	private void throwGameEndedExceptionWhenGameIsntOpen(GameEntity entity) throws GameEndedException {
		if (!entity.getGameState().equals(GameState.OPEN))
			throw new GameEndedException(entity.getGameState());
	}

	private void throwIsNotYourMoveExceptionWhenIsNotYourTurn(boolean isRedPlayer, GameEntity entity)
			throws IsNotYourMoveException {
		if (isRedPlayer != entity.isRedMove())
			throw new IsNotYourMoveException();
	}

	private void throwGameNotExistExceptionWhenTokensWasntFound(Optional<GameEntity> gameEntity)
			throws GameNotExistException {
		if (!gameEntity.isPresent())
			throw new GameNotExistException();
	}

	private Optional<GameEntity> findPlayerByYellowTokenIfRedTokenNotExist(PlayerMoveCommand playerMoveCommand,
			Optional<GameEntity> gameEntity) {
		if (!gameEntity.isPresent())
			gameEntity = gameEntityRepository.findByPlayerYellowToken(playerMoveCommand.getToken());
		return gameEntity;
	}

}
