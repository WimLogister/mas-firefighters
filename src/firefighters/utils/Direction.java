package firefighters.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * Direction 
 */

public class Direction {

	public int xDiff;
	public int yDiff;	
	
	/**
	 * Discretization of velocity vector in grid space
	 * Given a velocity vector, outputs the relative xDiff and yDiff coordinates
	 * Where 0 degrees corresponds to direction East, 90 to North, 180 to West and 270 to South
	 */	
	
	public int[] discretizeVector(Vector2 velocity){
		int[] result = fromAngleToDir(velocity.angle());
		this.xDiff = result[0];
		this.yDiff = result[1];
		return result;
	}
	
	/**
	 * Discretizatin of the angle in relative xDiff and yDiff coordinates of the grid
	 */
	
	public int[] fromAngleToDir(float angle){
		int[] result = new int[2];
		if(angle <= 22 || angle > 337) { // East
			result[0] = 1;
			result[1] = 0;
		}
		else if(angle <= 67 && angle > 22) { // North-East
			result[0] = 1;
			result[1] = 1;
		}
		else if(angle <= 112 && angle > 67) { // North
			result[0] = 0;
			result[1] = 1;
		}
		else if(angle <= 157 && angle > 112) { // North-West
			result[0] = -1;
			result[1] = 1;
		}
		else if(angle <= 202 && angle > 157) { // West
			result[0] = -1;
			result[1] = 0;
		}
		else if(angle <= 247 && angle > 202) { // South-West
			result[0] = -1;
			result[1] = -1;
		}
		else if(angle <= 292 && angle > 247) { // South
			result[0] = 0;
			result[1] = -1;
		}
		else if(angle <= 337 && angle > 292) { // South-East
			result[0] = 1;
			result[1] = -1;
		}
		this.xDiff = result[0];
		this.yDiff = result[1];
		return result;
	}
	
	public String toString(){
		return "(" + xDiff + "," + yDiff + ")";
	}
}
