package wt.connectfourgame.fixtures;

import java.util.ArrayList;
import java.util.List;

import wt.connectfourgame.model.states.Cell;
import wt.connectfourgame.model.states.CellState;

public class CellFixture {

	public static List<Cell> generateCellList(CellState... cellStates) {
		List<Cell> cellList = new ArrayList<Cell>();
		for (int i = 0; i < cellStates.length; i++)
			cellList.add(new Cell(cellStates[i], 0, 0));
		return cellList;
	}
}
