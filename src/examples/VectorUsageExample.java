package examples;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;

public class VectorUsageExample {

  private static final float MAX_VELOCITY = 3;

  public static void main(String[] args) {
	Vector2 velocity = new Vector2(1.5f, 2);
    Vector2 wind = new Vector2(0, 1);
    // The various method like add mutate the vector instead of making a new one, they return the vector
    // itself for easy chaining

    // Add a vector and make sure ||velocity|| <= MAX_VELOCITY
    velocity.add(wind).clamp(0, MAX_VELOCITY);	
    //System.out.println(velocity + " norm: " + velocity.len());
    
    
    Vector2 sample = new Vector2(5,0);
    System.out.println(sample.angle());
    sample.setAngle(45);
    System.out.println(sample.angle());
    sample.setAngle(45-90);

    System.out.println(sample.angle());
    
    System.out.println((int)Math.floor(Math.sqrt(10)));

  }

}
