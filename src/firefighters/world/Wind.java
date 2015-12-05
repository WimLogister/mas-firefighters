package firefighters.world;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import cern.jet.random.Uniform;
import firefighters.utils.Directions;
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
	private float changable; // Influence on how much the wind is changed every step
	
	public Wind(Grid<Object> grid, float windFactorSpeed, Directions direction, Float changable){
		this.grid = grid;
		Vector2 windVelocity = new Vector2();
		windVelocity.x = windFactorSpeed * SimulationConstants.MAX_WIND_SPEED;
		windVelocity.setAngle(Directions.fromDirToAngle(direction));
		this.velocity = windVelocity;
		this.changable = changable;
	}

	/**
	 * With each step some random noise is added so that the direction of the wind can change gradually over time
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void blow(){
		float variance = velocity.len() * changable;
		float mean = 0;
		java.util.Random r = new java.util.Random();
		float noiseSpeed = (float) (r.nextGaussian() * Math.sqrt(variance) + mean);
		float varianceA = velocity.angle() * changable;
		float noiseAngle = (float) (r.nextGaussian() * Math.sqrt(varianceA) + mean); 
		velocity.add(noiseSpeed, noiseAngle);
	}
	
	public Vector2 getVelocity(){
		return this.velocity;
	}
	
	public void setVelocity(Vector2 dir){
		this.velocity = dir;
	}
}