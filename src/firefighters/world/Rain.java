package firefighters.world;

import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.util.ContextUtils;

public class Rain {
	/*
	 * Rain usually appears in a few areas and is not just randomly scattered around the forest.
	 */
	
	private static final double RAIN_PROB = 0.1; // Chance with which rain can appear
	private static final Uniform urng = RandomHelper.getUniform();
	private Grid<Object> grid;
	
	public Rain(Grid<Object> grid){
		this.grid = grid;
	}
	
	/*
	 * Rain can appear in any part of the forest at any time 
	 * Chance with which rain appears can change over time?
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void appearRain(){
		if (urng.nextDouble() < RAIN_PROB) {
			Rain rain = new Rain(grid);
			addRandom(rain);
		}
	}
	
	// TODO: AddRandom
	public void addRandom(Rain rain){
		Context<Object> context = ContextUtils.getContext(this);
		GridDimensions dims = grid.getDimensions();
		int[] nextLoc = {RandomHelper.nextIntFromTo(0,dims.getDimension(0)),RandomHelper.nextIntFromTo(0,dims.getDimension(1))};
		context.add(rain);
		grid.moveTo(rain, nextLoc);
	}
}
