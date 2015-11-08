package firefighters.world;

import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;
import cern.jet.random.Uniform;
import constants.SimulationConstants;
import firefighters.agent.Agent;
import firefighters.utils.Directions;

/*
 * TODO:
 * Spreading the fire: can the fire spread to a grid which is occupied by a forester? Currently not.
 * Direction: currently fire takes direction of the wind in one timestep, other solutions might make more sense.
 * Appear(): now fires might randomly appear at already burned grids? Need to check this.
 */
public class Fire {
	
	private Grid<Object> grid;
	private Directions direction; // Influenced by wind
	private double speed; // Influenced by rain and hosing, probability with which it spreads, maximum speed = 1
	private static final Uniform urng = RandomHelper.getUniform();
	// Fire has certain number of lifePoints which decreases it is being hosed by an agent
	private int lifePoints;
	private int maxLifePoints;
	
	public Fire(Grid<Object> grid, Directions direction, double speed, int lifePoints, int maxLifePoints) {
		this.grid = grid;
		this.direction = direction;
		if(speed > 1 || speed < 0){
			throw new IllegalArgumentException("Speed value of fire is out of range!");
		} else this.speed = speed;
		this.lifePoints = lifePoints;
		this.maxLifePoints = lifePoints;
	}
	
	/**
	 * With each step: 
	 * Fires can be extinguished, method called by agent
	 * Fire burns down the trees (decreasing the lifepoints of the trees)
	 * Fire may change its speed according to rain and hosing
	 * May change its direction according to the direction of the wind
	 * Spreads to new area (according to direction) with certain chance
	 * Wildfires can appear suddenly in any part of the forest
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		burn();
		updateSpeed();
		updateDirection();
		spread();
		appear();
	}
	
	/**
	 *  If there is a firefighter hosing the fire, the fire decreases in its lifepoints and speed
	 */
	public void extinguish(){
		this.lifePoints--;
    if (this.lifePoints <= 0) {
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
		}
		else{
			setSpeed(speed - 0.2);
		}
	}
	
	/**
	 * If there is no rain in the direction of the movement of the wildfire, it speeds up. 
	 * If there is rain in its heading, then it slows down
	 * If there is rain at the location of the fire itself, speed slows down even more
	 * Can be adjusted according to what seems feasible with respect to reducing speed
	 */
	public void updateSpeed(){
		if(checkRainInHeading()) setSpeed(speed * 0.9);
		else setSpeed(speed * 1.1);
		if(checkRainInLocation()) setSpeed(speed - 0.1);
		else setSpeed(speed + 0.1);
	}

	/**
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
	
	/**
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
	
	/**
	 * Update the fires direction: fire takes direction of the wind
	 */
	public void updateDirection(){
		IndexedIterable<Wind> winds = ContextUtils.getContext(this).getObjects(Wind.class);
		Wind currentWind = winds.iterator().next();
		Directions windDirection = currentWind.getDirection();
    direction = windDirection;
	}
	
	/**
	 * Fire is burning the tree in the current grid (= decreasing its lifepoints)
	 */
	public void burn(){
		GridPoint pt = grid.getLocation(this);
		Tree treeToBurn = null;
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
			if (obj instanceof Tree){
				treeToBurn = (Tree) obj;
			}
		}
		if(!(treeToBurn == null)) treeToBurn.decrementLifePoints();
	}
	
	/**
	 * Fire spreads to the next grid in its direction with the highest chance (= speed), but can also spread to other directions with a lower chance
	 */
	public void spread(){
		GridPoint pt = grid.getLocation(this);
		double spreadChance = speed * 0.2; // Chance with which to spread to another direction than the fires own direction
		for(Directions dir : Directions.values()){
			int cX = pt.getX() + dir.xDiff;
			int cY = pt.getY() + dir.yDiff;
			int[] cLoc = {cX, cY}; // Get location of grid to which the fire possibly spreads
	
			if(canSpread(cLoc)){
				if(dir == direction){
					if (urng.nextDouble() < speed) { // Spreads with certain "speed" (modeled in stochastic way)
						Fire fire = new Fire(grid, direction, speed, lifePoints, maxLifePoints); // Fire spreads with same direction, speed and number of lifepoints
						ContextUtils.getContext(this).add(fire);
						grid.moveTo(fire, cX, cY);
					}
				}
				else{
					if(urng.nextDouble() < spreadChance){
						Fire fire = new Fire(grid, direction, speed, lifePoints, maxLifePoints);
						ContextUtils.getContext(this).add(fire);
						grid.moveTo(fire, cX, cY);
					}
				}
			}			
		}
	}
	
	/**
	 *  Can only spread to new area if this area is a forest (with no forester on it) which is not already burned (if so, it cannot spread to here)
	 */
	public boolean canSpread(int[] location){
		boolean spreadPossible = true;
		for (Object object : grid.getObjectsAt(location)){
			if(object instanceof Agent) spreadPossible = false;
			if(object instanceof Tree) if (((Tree) object).getLifePoints()<=0) spreadPossible = false;
			if(object instanceof Fire) spreadPossible = false;
		}
		return spreadPossible;
	}
	
	/**
	 * Fires can appear suddenly in any part of the forest with a certain chance
	 */
	public void appear(){
		if (urng.nextDouble() < SimulationConstants.FIRE_PROB) {
			RandomGridAdder<Object> ra = new RandomGridAdder<Object>();
			// New fire has maximum number of lifepoints
			Fire fire = new Fire(grid,Directions.getRandomDirection(),urng.nextDouble(),maxLifePoints,maxLifePoints);
			ContextUtils.getContext(this).add(fire);
			ra.add(grid, fire);
		}
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	/**
	 * Not possible to set speed lower than 0 or higher than 1
	 */
	public void setSpeed(double speed){
		if(speed < 0) speed = 0;
		if(speed > SimulationConstants.MAX_FIRE_SPEED) speed = SimulationConstants.MAX_FIRE_SPEED;
		this.speed = speed;
	}
	
	public Directions getDirection(){
		return this.direction;
	}
	
	public void setDirection(Directions direction){
		this.direction = direction;
	}
	
	public int getLifePoints(){
		return lifePoints;
	}
	
	/**
	 * Not possible to set lifepoints of fire higher than the maximum number of lifepoints
	 */	
	public void setLifePoints(int lifePoints){
		if(lifePoints > maxLifePoints) this.lifePoints = maxLifePoints;
		else this.lifePoints = lifePoints;
	}
}
