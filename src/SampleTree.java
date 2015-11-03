
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import repast.simphony.context.Context;


public class SampleTree {
	
	private Grid<Object> grid;
	private int lifePoints;
	
	public SampleTree(Grid<Object> grid, int lifePoints) {
		this.grid = grid;
		this.lifePoints = lifePoints;
	}
	
	/**
	 * To be called by SampleFire agents. Models trees being burned by the fire.
	 */
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
