package wt.connectfourgame.serialize;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.model.boards.Board;
import wt.connectfourgame.model.boards.Board7x6;
import wt.connectfourgame.model.boards.GameStateCalculateUsingDiagonals;

public class BoardSerializatorTest implements WithAssertions, WithBDDMockito {

	@Test
	public void isBoardCanBeSerialized() {
		Board board = new Board7x6(new GameStateCalculateUsingDiagonals());
		BoardSerializator boardSerializator = new BoardSerializator();
		byte[] boardBytes = boardSerializator.serializeBoardToBytes(board);
		assertThat(boardSerializator.deserializeBytesToBoard(boardBytes)).isEqualTo(board);
	}

}
