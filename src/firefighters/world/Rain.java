package firefighters.world;

import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;

/**
 * Very simple implementation of rain
 * Improvements:
 * Chance with which rain can appear can change over time (because weather can change over time)
 * Rain usually appears in a few areas and is not just randomly scattered around the forest.
 */
public class Rain {
	
	private double RAIN_PROB = 0.2; // Chance with which rain can appear
	private static final Uniform urng = RandomHelper.getUniform();
	private Grid<Object> grid;
	
	public Rain(Grid<Object> grid){
		this.grid = grid;
	}
	
	/**
	 * Rain can appear and disappear in any part of the forest at any time 
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void raining(){
		appearRain();
		disappearRain();
	}
	
	public void appearRain(){
		if (urng.nextDouble() < RAIN_PROB) {
			RandomGridAdder<Object> ra = new RandomGridAdder<Object>();
			Rain rain = new Rain(grid);
			ContextUtils.getContext(this).add(rain);
			ra.add(grid, rain);
		}
	}
	
	public void disappearRain(){
		if (urng.nextDouble() < RAIN_PROB) {
			ContextUtils.getContext(this).remove(this);
		}
	}
}
