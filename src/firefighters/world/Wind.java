package firefighters.world;

import repast.simphony.space.grid.Grid;
import firefighters.utils.Directions;

public class Wind {
	
	private Grid<Object> grid;
	private Directions direction; // Is global across forest
	
	public Wind(Grid<Object> grid, Directions direction){
		this.grid = grid;
		this.direction = direction;
	}
	
	/*
	 * Can change direction at any time
	 */
	public void changeDirection(Directions dir){
		this.direction = dir;
	}

}

