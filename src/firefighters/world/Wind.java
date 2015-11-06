package firefighters.world;

import repast.simphony.space.grid.Grid;
import firefighters.utils.Directions;

/*
 * Improvements/TODO
 * Can change direction at any time
 */
public class Wind {
	
	private Grid<Object> grid;
	private Directions direction; // Is global across forest
	
	public Wind(Grid<Object> grid, Directions direction){
		this.grid = grid;
		this.direction = direction;
	}
	
	public Directions getDirection(){
		return this.direction;
	}
	
	public void setDirection(Directions dir){
		this.direction = dir;
	}
}

