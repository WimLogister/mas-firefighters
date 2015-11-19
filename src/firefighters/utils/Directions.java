package firefighters.utils;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public enum Directions {
  NORTH(0, 1),
  NORTH_EAST(1, 1),
  EAST(1, 0),
  SOUTH_EAST(1, -1),
  SOUTH(0, -1),
  SOUTH_WEST(-1, -1),
  WEST(-1, 0),
  NORTH_WEST(-1, 1);

  public final int xDiff, yDiff;

  private Directions(int xDiff, int yDiff) {
    this.xDiff = xDiff;
    this.yDiff = yDiff;
  }

  public static Directions getRandomDirection() {
    Random random = new Random();
    return values()[random.nextInt(values().length)];
  }

  public static Directions findDirection(int xDiff, int yDiff) {
    for (Directions direction : Directions.values()) {
      if (direction.xDiff == xDiff && direction.yDiff == yDiff)
        return direction;
    }
    throw new IllegalArgumentException("Invalid direction: x: " + xDiff + " y " + yDiff);
  }

  /**
   * Given a velocity vector outputs the relative xDiff and yDiff coordinates according to the vector's degree 0 degrees
   * corresponds to direction East, 90 to North, 180 to West and 270 to South
   */
  public static Directions fromVectorToDir(Vector2 velocity) {
    Directions dir = fromAngleToDir(velocity.angle());
    return dir;
  }

  /**
   * Projection of an angle to the corresponding direction
   */
  public static Directions fromAngleToDir(float angle) {
    Directions dir = null;
    if (angle <= 22 || angle > 337)
      dir = Directions.EAST;
    else if (angle <= 67 && angle > 22)
      dir = Directions.NORTH_EAST;
    else if (angle <= 112 && angle > 67)
      dir = Directions.NORTH;
    else if (angle <= 157 && angle > 112)
      dir = Directions.NORTH_WEST;
    else if (angle <= 202 && angle > 157)
      dir = Directions.WEST;
    else if (angle <= 247 && angle > 202)
      dir = Directions.SOUTH_WEST;
    else if (angle <= 292 && angle > 247)
      dir = Directions.SOUTH;
    else if (angle <= 337 && angle > 292)
      dir = Directions.SOUTH_EAST;
    return dir;
  }

  public String toString() {
    return "(" + xDiff + "," + yDiff + ")";
  }
}