package wt.connectfourgame.model.boards;

public class Board7x6 extends BoardTemplate {

	private static final long serialVersionUID = 1L;
	private static final int xSize = 7;
	private static final int ySize = 6;

	public Board7x6(GameStateCalculateStrategy gameStateCalculateStrategy) {
		super(xSize, ySize, gameStateCalculateStrategy);
	}
	
	

}
