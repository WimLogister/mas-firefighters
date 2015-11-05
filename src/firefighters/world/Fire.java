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
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

/*
 * Fire can appear out of nothing.
 */

public class Fire {
	
	private Grid<Object> grid;
	private Directions direction; // Influenced by wind
	private double speed; // Influenced by rain and hosing, probability with which it spreads, maximum speed = 1
	
	public Fire(Grid<Object> grid, Directions direction, int speed) {
		this.grid = grid;
		this.direction = direction;
		if(speed > 1 || speed < 0){
			throw new IllegalArgumentException("Speed value of fire is out of range!");
		} else this.speed = speed;
	}
	
	/*
	 * With each step the fire:
	 * May change its speed according to rain and hosing
	 * May change its direction according to the direction of the wind
	 * May decrease in lifepoints due to hosing
	 * Spreads to new area (according to direction) with certain chance
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		updateSpeed();
		updateDirection();
		
	}
	
	public void extinguish(){
		ContextUtils.getContext(this).remove(this);
	}
	
	public void updateSpeed(){
		
	}
	
	public void updateDirection(){
		
	}
	
	/*
	 * Fire is burning
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
}
