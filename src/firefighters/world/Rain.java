package firefighters.world;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;

/**
 *  Rain appears as a group of rain-objects, covering multiple grid-cells.
 *  Rain appears outside the forest grid and travels through it.
 *  Rain moves with the same direction of the wind and its speed slightly lower than the speed of the wind.
 */
public class Rain {
	
	private static final Uniform urng = RandomHelper.getUniform();
	private Grid<Object> grid;
	private Vector2 velocity;
	
	public Rain(Grid<Object> grid, Vector2 velocity){
		this.grid = grid;
		this.velocity = velocity;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void raining(){
		// Update velocity vector of the rain
		
	}
}
