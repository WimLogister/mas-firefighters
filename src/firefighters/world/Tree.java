package firefighters.world;

import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import repast.simphony.context.Context;


public class Tree {
	
	private Grid<Object> grid;
	private int lifePoints;
	
	public Tree(Grid<Object> grid, int lifePoints) {
		this.grid = grid;
		this.lifePoints = lifePoints;
	}
	
	public void decrementLifePoints() {
		this.lifePoints--;
		if (this.lifePoints-- <= 0) {
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
		}
	}
	
	public int getLifePoints() {
		return lifePoints;
	}

}
