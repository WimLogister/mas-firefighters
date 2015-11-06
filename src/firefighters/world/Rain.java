package firefighters.world;

import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;

/*
 * Improvements:
 * Chance with which rain can appear can change over time
 */

public class Rain {
	/*
	 * Rain usually appears in a few areas and is not just randomly scattered around the forest.
	 */
	
	private static final double RAIN_PROB = 0; // Chance with which rain can appear
	private static final Uniform urng = RandomHelper.getUniform();
	private Grid<Object> grid;
	
	public Rain(Grid<Object> grid){
		this.grid = grid;
	}
	
	/*
	 * Rain can appear in any part of the forest at any time 
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void appearRain(){
		if (urng.nextDouble() < RAIN_PROB) {
			RandomGridAdder<Object> ra = new RandomGridAdder<Object>();
			Rain rain = new Rain(grid);
			ContextUtils.getContext(this).add(rain);
			ra.add(grid, rain);
		}
	}
	
	public void disappearRain(){
		
	}
}
