package firefighters.world;

import constants.SimulationConstants;
import cern.jet.random.Uniform;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import firefighters.utils.Directions;

public class Wind {
	
	private Grid<Object> grid;
	private Directions direction; // Is global across forest
	private static final Uniform urng = RandomHelper.getUniform();	
	
	public Wind(Grid<Object> grid, Directions direction){
		this.grid = grid;
		this.direction = direction;
	}
	
	/**
	 * Wind can change its direction (randomly) at any time
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void blow(){
		if (urng.nextDouble() < SimulationConstants.WIND_CHANGE_PROB) {
			direction = Directions.getRandomDirection();
		}	
	}
	
	public Directions getDirection(){
		return this.direction;
	}
	
	public void setDirection(Directions dir){
		this.direction = dir;
	}
}

