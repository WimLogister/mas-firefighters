package firefighters.utils;

import java.util.Random;

public enum Directions {
	NORTH(0,1), EAST(1,0), SOUTH(0,-1), WEST(-1,0);
	
	public int xDiff, yDiff;
	
	private Directions(int xDiff, int yDiff) {
		this.xDiff = xDiff;
		this.yDiff = yDiff;
	}
	
	public static Directions getRandomDirection(){
		Random random = new Random();
		return values()[random.nextInt(values().length)];
	}
}
