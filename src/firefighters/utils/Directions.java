package firefighters.utils;

import java.util.Random;

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
	
	public static Directions getRandomDirection(){
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
}
