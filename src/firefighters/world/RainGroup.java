package firefighters.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;


/**
 * Contains multiple rain-objects
 * Travels for a certain amount of time
 */
public class RainGroup {

	private int size; // Number of rain objects in the "cloud"
	private Context<Object> context;
	private Grid<Object> grid;
	private int strength; // Value in range [1,3] to indicate its strength
	private int tick;
	private int maxTick;
	private int[] location;
	private int gridSize;
	private ArrayList<Rain> rainObjects = new ArrayList<Rain>();
	Random rand = new Random();
	
	/**
	 * Raingroup contains certain amount of rain-objects and travels for a certain amount of time
	 * which is determined by its strengh (the stronger the rain, the bigger the size of the raingroup
	 * and the longer it will travel)
	 */
	public RainGroup(Context<Object> context, Grid<Object> grid, int strength, int[] location,int gridSize){
		this.context = context;
		this.grid = grid;
		this.strength = strength;
		this.location = location;
		this.tick = 0;
		this.gridSize = gridSize;
		int maxSize;
		int minSize;
		int maxMTick;
		int minMTick;
		// Determine the ranges by the strength of the rain
		if(strength == 1) {
			maxMTick = 100;
			minMTick = 50;
			maxSize = (int) (gridSize * gridSize * 0.1);
			minSize = (int) (gridSize * gridSize * 0.05);
		}
		else if(strength == 2) {
			maxMTick = 200;
			minMTick = 100;
			maxSize = (int) (gridSize * gridSize * 0.3);
			minSize = (int) (gridSize * gridSize * 0.1);
		}
		else if(strength == 3) {
			maxMTick = 300;
			minMTick = 20;
			maxSize = (int) (gridSize * gridSize * 0.5);
			minSize = (int) (gridSize * gridSize * 0.3);
		}
		else throw new IllegalArgumentException("Strength value of rain is out of range!");	
		this.maxTick = rand.nextInt((maxMTick - minMTick) + 1) + minMTick;
		this.size = rand.nextInt((maxSize - minSize) + 1) + minSize;
		fillRain(context, location);
	}	
	
	/**
	 * Raingroup is visualized as a square, fill this grid with rain-objects
	 */
	public void fillRain(Context<Object> context, int[] location){
		// Get width and height of grid
		int width = (int) Math.floor(Math.sqrt(size));
		int rest = size - (width*width);
		
		// Fill the square		
		for(int x=location[0]; x<location[0]+width; x++){
			for(int y=location[1]; y<location[1]+width; y++){
				Rain rain = new Rain(grid);
				context.add(rain);
				int[] newLoc = {x,y};
				grid.moveTo(rain,newLoc);
				rainObjects.add(rain);
				
				
			}
		}
		
		// Add the remaining rain-objects in random places around
		// Make list of locations to choose from
		ArrayList<int[]> toChooseFrom = new ArrayList<int[]>();
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
		
		Collections.shuffle(toChooseFrom);
		for(int i=0;i<rest;i++){
			Rain rain = new Rain(grid);
			context.add(rain);
			grid.moveTo(rain, toChooseFrom.get(i));
			rainObjects.add(rain);
		}	
	}
	
	public int getTick(){
		return tick;
	}
	
	public int getMaxTick(){
		return maxTick;
	}
	
	public ArrayList<Rain> getRainObjects(){
		return rainObjects;
	}
	
	public void incrementTick(){
		this.tick++;
	}
	
	public boolean containsRain(int[] loc){
		boolean containsRain = false;
		for (Object object : grid.getObjectsAt(loc)){
			if(object instanceof Rain) containsRain = true;
		}
		return containsRain;
	}
}
