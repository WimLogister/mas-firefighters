package firefighters.world;


import java.util.List;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;


public class Fire {
	
	private enum Direction {NORTH,EAST,SOUTH,WEST}; 
	
	private Grid<Object> grid;
	private int[] position = new int[2]; //coordinates in the grid
	private Direction direction;
	private int speed;
	
	public Fire(Grid<Object> grid) {
		this.grid = grid;
	}
}
