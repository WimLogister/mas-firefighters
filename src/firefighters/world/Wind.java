package firefighters.world;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationParameters;
import firefighters.utils.Directions;

/**
 * The wind is global across the forest
 * The wind its direction changes gradually over time
 * TODO: visualisation of the wind in the grid
 */
public class Wind {
	
	private Grid<Object> grid;
	// Velocity vector with speed and direction 
	// These values are global across the forest
	private static Vector2 velocity; 
	private float changable; // Influence on how much the wind is changed every step
	
	public static Vector2 getWindVelocity(){
		return velocity;
	}
	
	public Wind(Grid<Object> grid, float windFactorSpeed, Directions direction, Float changable){
		this.grid = grid;
		Vector2 windVelocity = new Vector2();
    windVelocity.x = windFactorSpeed * SimulationParameters.maxWindSpeed;
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
    velocity.clamp(0, SimulationParameters.maxWindSpeed);
	}
	
	public Vector2 getVelocity(){
		return this.velocity;
	}
	
	public void setVelocity(Vector2 dir){
		this.velocity = dir;
	}
}