package wt.connectfourgame.model.boards;

import wt.connectfourgame.model.states.GameState;

public interface GameStateCalculateStrategy {
	
	public void prepare(BoardTemplate boardTemplate);
	public GameState calculate();

}
