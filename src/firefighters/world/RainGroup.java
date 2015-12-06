package firefighters.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import lombok.Getter;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationParameters;
import firefighters.utils.Directions;
import firefighters.utils.GridFunctions;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

/**
 * Contains multiple rain-objects
 */
public class RainGroup {

	private int size; // Number of rain objects in the "cloud"
	private Context<Object> context;
	private Grid<Object> grid;
	@Getter
	private ArrayList<Rain> rainObjects = new ArrayList<Rain>();
	Random rand = new Random();
	private List<int[]> stillToAppear = new ArrayList<int[]>();
	
	/**
	 * Raingroup contains certain amount of rain-objects
	 */
	public RainGroup(Context<Object> context, Grid<Object> grid, int[] location){
		this.context = context;
		this.grid = grid;
		int averageSize = SimulationParameters.averageRainSize;
		// Size can vary with 10 percent of the average size given
		int plusOrMin = rand.nextInt(2);
		if(plusOrMin==0) this.size = (int) ((int) averageSize + rand.nextDouble() * 0.1);
		else this.size = (int) ((int) averageSize - rand.nextDouble() * 0.1);
		fillRain(location);
	}	
	
	@ScheduledMethod(start = 1, interval = 1, priority =0)
	public void step(){
		Vector2 windVelocity = Wind.getWindVelocity();
		Directions dir = Directions.fromVectorToDir(windVelocity);
		List<int[]> newLocs = new ArrayList<int[]>();
		for(int[] loc : stillToAppear){
			int x = loc[0] + dir.xDiff;
			int y = loc[1] + dir.yDiff;
			checkAddInGrid(x,y);
		}
		stillToAppear = newLocs;
	}
	
	/**
	 * Raingroup is visualized as a square, fill this grid with rain-objects
	 */
	public void fillRain(int[] location){
		// Get width and height of grid
		//int width = (int) Math.floor(Math.sqrt(size));
		//int rest = size - (width*width);
		int width = size;
		// Fill the square		
		for(int x=location[0]; x<location[0]+width; x++){
			for(int y=location[1]; y<location[1]+width; y++){
				checkAddInGrid(x,y);			
			}
		}
		
		// Add the remaining rain-objects in random places around
		// Make list of locations to choose from
		/*ArrayList<int[]> toChooseFrom = new ArrayList<int[]>();
		// Start, down left
		int y=location[1]-1;
		for(int x = location[0]-1; x<location[0]+width; x++){
			int[] newLoc = {x,y};
			toChooseFrom.add(newLoc);
		}
		// down right
		int x2 = location[0] + width;
		for(int y2=y; y2<location[1]+width;y2++){
			int[] newLoc = {x2,y2};
			toChooseFrom.add(newLoc);
		}
		// up right
		int y3 = location[1] + width;
		for(int x3 = x2; x3>location[0]-1;x3--){
			int[] newLoc = {x3,y3};
			toChooseFrom.add(newLoc);
		}
		// up left
		int x4 = location[0] - width;
		for(int y4=y3; y4>location[1]-1;y4--){
			int[] newLoc = {x4,y4};
			toChooseFrom.add(newLoc);
		}
		
		// Choose random locations
		Collections.shuffle(toChooseFrom);
		for(int i=0;i<rest;i++){
			int x = toChooseFrom.get(i)[0];
			int y1 = toChooseFrom.get(i)[1];
			checkAddInGrid(x,y1);
		}	*/
	}
	
	public void checkAddInGrid(int x, int y){
		int[] newLoc = {x,y};
		if(!GridFunctions.isWithinBounds(x, y)) stillToAppear.add(newLoc);
		else {
			Rain rain = new Rain(grid);
			context.add(rain);
			TreeBuilder.performance.increaseRainCount();
			grid.moveTo(rain, x, y);
			rainObjects.add(rain);
		}
	}
	
	public boolean containsRain(int[] loc){
		boolean containsRain = false;
		for (Object object : grid.getObjectsAt(loc)){
			if(object instanceof Rain) containsRain = true;
		}
		return containsRain;
	}
	
	public void removeRain(Rain rain){
		TreeBuilder.performance.decreaseRainCount();
		context.remove(rain);
		rainObjects.remove(rain);
	}
}
