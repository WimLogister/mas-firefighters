
import java.util.List;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;


public class SampleFire {
	
	//Changed by Nadine
	
	private static final double BURN_PROB = 0.9;
	private static final double SPREAD_PROB = 0.1;
	private static final Uniform urng = RandomHelper.getUniform();
	
	private Grid<Object> grid;
	
	public SampleFire(Grid<Object> grid) {
		this.grid = grid;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void burn() {
		
		GridPoint currpt = grid.getLocation(this);
		// An object that lets us access the surroundings of the current cell
		// and filter on parameter entities.
		GridCellNgh<SampleTree> nghCreator = new GridCellNgh<>(grid, currpt, SampleTree.class, 1, 1);
		
		List<GridCell<SampleTree>> gridCells = nghCreator.getNeighborhood(true);
		
		Context<Object> context = ContextUtils.getContext(this);
		
		/*
		 * Model burning and spreading
		 */
		for (GridCell<SampleTree> cell : gridCells) {

			GridPoint otherpt = cell.getPoint();
			// Burn trees in current cell 
			if (otherpt.getX() == currpt.getX() && otherpt.getY() == currpt.getY()) {
				/*
				 * If all trees in this cell are burned, fire dies out and this fire
				 * agent has to be removed from the simulation
				 */
				if (cell.size() <= 0) {
					context.remove(this);
				}
				System.out.printf("Number of trees in cell: %d", cell.size());
				
				// Decrement life points in all trees in this forest
				for (SampleTree tree : cell.items()){
					if (urng.nextDouble() < BURN_PROB) {
						tree.decrementLifePoints();
					}
				}
				
			}
			// Spread to neighboring cells
			else {
				boolean spread = false;
				for (SampleTree tree : cell.items()) {
					// If fire spreads, create a new agent and add it to the context
					if (urng.nextDouble() < SPREAD_PROB) {
						SampleFire fire = new SampleFire(grid);
						context.add(fire);
						grid.moveTo(fire, otherpt.getX(), otherpt.getY());
					}
				}
			}
		}
	}

}
