package firefighters.world;
import java.util.List;

import cern.jet.random.Uniform;
import firefighters.utils.Directions;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

/*
 * Fire can appear out of nothing.
 */

public class Fire {
	
	private static final double FIRE_PROB = 0.05; // Chance with which fire can appear out of nowhere
	private Grid<Object> grid;
	private Directions direction; // Influenced by wind
	private double speed; // Influenced by rain and hosing, probability with which it spreads, maximum speed = 1
	private static final Uniform urng = RandomHelper.getUniform();
	
	public Fire(Grid<Object> grid, Directions direction, double speed) {
		this.grid = grid;
		this.direction = direction;
		if(speed > 1 || speed < 0){
			throw new IllegalArgumentException("Speed value of fire is out of range!");
		} else this.speed = speed;
	}
	
	/*
	 * With each step 
	 * Wildfires can appear suddenly in any part of the forest
	 * May change its speed according to rain and hosing
	 * May change its direction according to the direction of the wind
	 * May decrease in lifepoints due to hosing
	 * Spreads to new area (according to direction) with certain chance
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		appear();
		updateSpeed();
		updateDirection();
		burn();
		spread();
	}
	
	public void extinguish(){
		ContextUtils.getContext(this).remove(this);
	}
	
	/*
	 * If there is no rain in the direction of the movement of the wildfire, it speeds up. 
	 * If there is rain in its heading, then it slows down
	 */
	public void updateSpeed(){
		
	}
	
	public void updateDirection(){
		
	}
	
	/*
	 * Fire is burning the trees
	 */
	public void burn(){
		
	}
	
	/*
	 * Fire is spreading
	 */
	public void spread(){
		// Can only spread to new area if this area is a forest (with no forester on it) which is not already burned (if so, it cannot spread to here)
		// (So to begin with easy assumption: cannot spread to a grid which is occupied by a forester)
		
	}
	
	/*
	 * Fires can appear suddenly in any part of the forest
	 */
	public void appear(){
		if (urng.nextDouble() < FIRE_PROB) {
			Fire fire = new Fire(grid,Directions.getRandomDirection(),urng.nextDouble());
			// TODO: AddRandom
			addRandom(fire);
		}
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	public void setSpeed(double speed){
		if(speed > 1 || speed < 0){
			throw new IllegalArgumentException("Speed value of fire is out of range!");
		} else this.speed = speed;
	}
	
	public Directions getDirection(){
		return this.direction;
	}
	
	public void setDirection(Directions direction){
		this.direction = direction;
	}
	
	// TODO: AddRandom
	public void addRandom(Fire fire){
		Context<Object> context = ContextUtils.getContext(this);
		GridDimensions dims = grid.getDimensions();
		int[] nextLoc = {RandomHelper.nextIntFromTo(0,dims.getDimension(0)),RandomHelper.nextIntFromTo(0,dims.getDimension(1))};
		context.add(fire);
		grid.moveTo(fire, nextLoc);
	}
}
