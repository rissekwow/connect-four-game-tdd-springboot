package wt.connectfourgame.serialize;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import wt.connectfourgame.model.boards.Board;

@Component
public class BoardSerializator {

	public byte[] serializeBoardToBytes(Board board) {
		return SerializationUtils.serialize(board);
	}

	public Board deserializeBytesToBoard(byte[] boardBytes) {
		return (Board) SerializationUtils.deserialize(boardBytes);
	}
}
