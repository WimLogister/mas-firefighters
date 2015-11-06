package firefighters.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cern.jet.random.Uniform;
import firefighters.agent.Agent;
import firefighters.utils.Directions;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

/*
 * Improvements
 * Adding "lifepoints" to the fire
 * Spreading the fire: now fire cannot spread to a grid which is occupied by a forester. Could be implemented?
 * Spreading direction: fire spreads with the direction of the wind with the highest chance, but can also spread to other directions with a lower chance
 */

public class Fire {
	
	private static final double FIRE_PROB = 0; // Chance with which fire can appear out of nowhere
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
	 * Burns down the trees (decreasing lifepoints of trees)
	 * May change its direction according to the direction of the wind
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
	
	/*
	 *  If there is a firefighter hosing the fire speed reduces
	 *  How "strong" the firefighter is can be modelled by increasing/decreasing the increasment of the fire
	 */
	public void extinguish(){
		ContextUtils.getContext(this).remove(this);
		setSpeed(speed - 0.2);
	}
	
	/*
	 * If there is no rain in the direction of the movement of the wildfire, it speeds up. 
	 * If there is rain in its heading, then it slows down
	 * If there is rain at the location itself speed slows down even more
	 * Can be adjusted according to what seems feasible with respect to reducing speed
	 */
	public void updateSpeed(){
		if(checkRainInHeading()) setSpeed(speed * 0.9);
		else setSpeed(speed * 1.1);
		if(checkRainInLocation()) setSpeed(speed - 0.1);
		else setSpeed(speed + 0.1);
		System.out.println("Speed " + speed);
	}

	/*
	 * Method to check if it is raining, either in the direction in which the fire is going to
	 * (passing true to the method) or at the current location of the fire itself (passing false to the method).
	 */
	public boolean checkRainInHeading(){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Rain> ngh = new GridCellNgh<>(grid, pt, Rain.class, 1, 1);
		List<GridCell<Rain>> gridCells = ngh.getNeighborhood(false);
		
		// If there is no rain at all, return false
		if(gridCells.size()==0) return false;
		
		// If there is rain in the grid the fire is heading to, return true
		boolean isRaining = false;
		for (GridCell<Rain> cell : gridCells) {
			for (Rain rain : cell.items()) {
				GridPoint rainpt = grid.getLocation(rain);  
				if (rainpt.getX() == pt.getX()+direction.xDiff && rainpt.getY() == pt.getY()+direction.yDiff) isRaining = true;
			}
		}
		return isRaining;
	}
	
	/*
	 * Check if it's raining in the current location of the fire
	 */
	
	public boolean checkRainInLocation(){
		boolean isRaining = false;
		GridPoint pt = grid.getLocation(this);
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
			if (obj instanceof Rain) isRaining = true;
		}	
		return isRaining;
	}
	
	/*
	 * Update the fires direction: fire takes direction of the wind
	 */
	public void updateDirection(){
		IndexedIterable<Wind> winds = ContextUtils.getContext(this).getObjects(Wind.class);
		Wind currentWind = winds.iterator().next();
		Directions windDirection = currentWind.getDirection();
		direction.xDiff = windDirection.xDiff;
		direction.yDiff = windDirection.yDiff;
	}
	
	/*
	 * Fire is burning the trees in the current grid thus decreasing their lifepoints
	 */
	public void burn(){
		GridPoint pt = grid.getLocation(this);
		Iterable<Object> trees = grid.getObjectsAt(pt.getX(), pt.getY());
		//for (final Object obj : trees){
		//Iterable<Object> trees = grid.getObjectsAt(pt.getX(),pt.getY());
		Iterator<Object> objects = trees.iterator(); 
		Object obj;
		while(objects.hasNext()){
			obj = objects.next();
			if (obj instanceof Tree){
				((Tree) obj).decrementLifePoints();
			}
		}	
	}
	
	/*
	 * Fire is spreading
	 */
	public void spread(){
		// Get the location to which the fire wants to spread according to its direction
		GridPoint pt = grid.getLocation(this);
		int cX = pt.getX() + direction.xDiff;
		int cY = pt.getY() + direction.yDiff;
		int[] cLoc = {cX, cY};
		// Can only spread to new area if this area is a forest (with no forester on it) which is not already burned (if so, it cannot spread to here)
		boolean spreadPossible = true;
		for (Object object : grid.getObjectsAt(cLoc)){
			if(object instanceof Agent) spreadPossible = false;
			if(object instanceof Tree) if (((Tree) object).getLifePoints()<=0) spreadPossible = false;
		}
		if(spreadPossible){
			// Spreads with certain "speed" (modeled in stochastic way)
			if (urng.nextDouble() < speed) {
				// Fire spreads with same direction and speed
				Fire fire = new Fire(grid, direction, speed);
				ContextUtils.getContext(this).add(fire);
				grid.moveTo(fire, cX, cY);
			}
		}
	}
	
	/*
	 * Fires can appear suddenly in any part of the forest with a certain chance
	 */
	public void appear(){
		if (urng.nextDouble() < FIRE_PROB) {
			RandomGridAdder<Object> ra = new RandomGridAdder<Object>();
			Fire fire = new Fire(grid,Directions.getRandomDirection(),urng.nextDouble());
			ContextUtils.getContext(this).add(fire);
			ra.add(grid, fire);
		}
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	/*
	 * Not possible to set speed lower than 0 or higher than 1
	 */
	public void setSpeed(double speed){
		if(speed < 0) speed = 0;
		if(speed > 1) speed = 1;
		this.speed = speed;
	}
	
	public Directions getDirection(){
		return this.direction;
	}
	
	public void setDirection(Directions direction){
		this.direction = direction;
	}
}
