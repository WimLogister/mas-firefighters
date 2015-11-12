package firefighters.world;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import cern.jet.random.Uniform;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

/**
 * The wind is global across the forest
 * The wind its direction changes gradually over time
 * TODO: visualisation of the wind in the grid
 */
public class Wind {
	
	private Grid<Object> grid;
	// Velocity vector with speed and direction 
	// These values are global across the forest
	private Vector2 velocity; 
	
	public Wind(Grid<Object> grid, Vector2 velocity){
		this.grid = grid;
		this.velocity = velocity;
	}
	
	/**
	 * With each step some random noise is added so that the direction of the wind changes gradually over time
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void blow(){
		/*
		double variance = direction.len();
		double mean = direction.
		java.util.Random r = new java.util.Random();
		double noise = r.nextGaussian() * Math.sqrt(variance) + mean;
		*/
	}
	
	public Vector2 getDirection(){
		return this.velocity;
	}
	
	public void setDirection(Vector2 dir){
		this.velocity = dir;
	}
}

