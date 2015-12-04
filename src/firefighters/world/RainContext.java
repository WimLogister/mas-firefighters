package firefighters.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import constants.SimulationParameters;
import cern.jet.random.Uniform;
import firefighters.utils.Directions;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;


public class RainContext {
	
	private Grid<Object> grid;
	private double appearChance;
	private int noRainGroups;
	private static final Uniform urng = RandomHelper.getUniform();
	Random rand = new Random();
	private int gridSize;
	private ArrayList<RainGroup> rainGroups = new ArrayList<RainGroup>();
	private int strength;
	
	public RainContext(Grid<Object> grid, double appearChance, int gridSize, int strength){
		this.grid = grid;
		this.appearChance = appearChance;
		this.noRainGroups = 0;
		this.gridSize = gridSize;
		this.strength = strength;
	}
	
	/**
	 * Rain clouds appear with a certain chance, influenced by the weather
	 * For every rain cloud in the grid the velocity of every rain object is updated
	 * Rain clouds are removed if they have passed a certain time
	 */	
	@ScheduledMethod(start = 1, interval = 1, priority =0)
	public void rain(){
		// Let new raingroups appear with a certain chance
		double chance = appearChance;
		if(noRainGroups > 0) chance = (chance / (noRainGroups)) * 0.01;
		double f = urng.nextDouble();
		if (f < chance) {
			// Let rain appear 
			int x = rand.nextInt((gridSize - 0) + 1);
			int y = rand.nextInt((gridSize - 0) + 1);
			int[] newLoc = {x,y};
			// Let new raingroup appear in random location
			RainGroup rg = new RainGroup(ContextUtils.getContext(this),grid,strength,newLoc);
			noRainGroups++;
			rainGroups.add(rg);
		}
		
		ArrayList<RainGroup> toRemove = new ArrayList<RainGroup>();
		for(RainGroup rg : rainGroups){
			// Get velocity vector of the rain
			float x = getCurrentWindVelocity().x; 
			float y = getCurrentWindVelocity().y;
			Vector2 velRain = new Vector2(x,y);	
			velRain.setLength(getCurrentWindVelocity().len()*0.9f); // Rain speed is a bit lower than that of the wind
			
			List<Rain> toRemove1 = new ArrayList<Rain>();
			// Let rain be carried by the wind
			if (urng.nextDouble() < velRain.len()) {
				for(Rain rain: rg.getRainObjects()){
					Directions dir = Directions.fromVectorToDir(velRain);	
					GridPoint pt = grid.getLocation(rain);
					int cX = pt.getX() + dir.xDiff;
					int cY = pt.getY() + dir.yDiff;
					
					// If new rain-location is out of borders, delete this rain object
					// In this way the cloud "travels" out of the grid
					if(cX < 0 || cX >= SimulationParameters.gridSize || cY < 0 || cY >= SimulationParameters.gridSize){
						toRemove1.add(rain);
					}
					else grid.moveTo(rain, cX, cY);
				}
			}
			
			for(Rain r : toRemove1){
				rg.removeRain(r);
			}
			
			// Remove the raingroup from the grid and from our list if it passed a certain max time
			if(rg.getTick()>rg.getMaxTick()) {
				for(Rain rain : rg.getRainObjects()){
					ContextUtils.getContext(this).remove(rain);
				}
				ContextUtils.getContext(this).remove(rg);
				toRemove.add(rg);
			}
			rg.incrementTick();
		}
		
		// Remove the raingroups from our list which were removed from the context
		for(RainGroup rg : toRemove){
			rainGroups.remove(rg);
			noRainGroups--;
		}
	}

	public Vector2 getCurrentWindVelocity(){
		IndexedIterable<Wind> winds = ContextUtils.getContext(this).getObjects(Wind.class);
		Wind currentWind = winds.iterator().next();
		return currentWind.getVelocity();
	}
}
