package wt.connectfourgame.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;
import wt.connectfourgame.model.states.GameState;

@Entity
@Data
public class GameEntity {

	@Id
	@GeneratedValue
	private long gameId;

	private String playerRedToken;
	private String playerYellowToken;

	private boolean isRedMove;


	@Enumerated(EnumType.STRING)
	private GameState gameState;

	@Lob
	private byte[] serializedBoard;

}
