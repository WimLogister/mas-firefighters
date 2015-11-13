package firefighters.world;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import cern.jet.random.Uniform;
import firefighters.agent.Agent;
import firefighters.utils.Direction;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

/**
 *  Rain appears as a group of rain-objects, covering multiple grid-cells.
 *  Rain appears outside the forest grid and travels through it.
 *  Rain moves with the same direction of the wind and its speed slightly lower than the speed of the wind.
 */
public class Rain {
	
	private static final Uniform urng = RandomHelper.getUniform();
	private Grid<Object> grid;
	
	public Rain(Grid<Object> grid){
		this.grid = grid;
	}
}
