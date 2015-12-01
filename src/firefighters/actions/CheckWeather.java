package firefighters.actions;

import static firefighters.utils.GridFunctions.findShortestPath;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import search.Path;

import com.badlogic.gdx.math.Vector2;

import lombok.Getter;
import firefighters.agent.Agent;
import firefighters.information.WeatherInformation;
import firefighters.pathfinding.GridAction;
import firefighters.pathfinding.GridState;
import firefighters.utils.Directions;
import firefighters.utils.GridFunctions;

public class CheckWeather implements PrimitiveAction{
	
	@Getter
	private static WeatherInformation weather;
	//@Getter
	//private int lastChecked
	
	@Override
	public void execute(Agent agent) {
		weather = agent.checkWeather();
		List<GridPoint> fires = agent.lookForFires();
		List<GridPoint> calculatedFirePoints = new ArrayList<GridPoint>();
		if(fires.size() > 0){
			for(GridPoint fire : fires){
				GridPoint newLoc = calculateNewFirePosition(fire,weather,agent);
				if(newLoc != null) calculatedFirePoints.add(newLoc);
			}
		}
		agent.setKnownFireLocations(calculatedFirePoints);
	}

	@Override
	/** Agent can always check the weather */
	public boolean checkPreconditions(Agent agent) {
		return true;
	}
	
	public GridPoint calculateNewFirePosition(GridPoint fireLocation, WeatherInformation weather, Agent agent){
		Vector2 wind = weather.getWind();
		// Get shortest path to current fire position to know how many steps we need to take into consideration
		Grid<?> grid = agent.getGrid();
		GridPoint agentPosition = grid.getLocation(agent);
		Path<GridState, GridAction> pathToFire = findShortestPath(grid, agentPosition, fireLocation); 
		int noOfSteps;
		//System.out.println("path to fire " + pathToFire);
		if(pathToFire.equals(null)) noOfSteps = 0;
		else if(!pathToFire.isValidPath()) noOfSteps = 0;
		else noOfSteps = pathToFire.getRoute().size()/2;	     
		
		if(noOfSteps>0){
			// Calculate where this fire will be assuming that the wind stays the same given the number of steps it 
			// takes for the firefighter to be there
			Directions dir = Directions.fromVectorToDir(wind);
			int xDiff = dir.xDiff*noOfSteps;
			int yDiff = dir.yDiff*noOfSteps;
		
			//System.out.println("Real fire location: " + actualFireLocation.getX() + "," + actualFireLocation.getY());
			int newX = fireLocation.getX() + xDiff;
			int newY = fireLocation.getY() + yDiff;
			//System.out.println("Predicted fire location: " + newX + "," + newY);
			
			GridPoint newPoint = new GridPoint(GridFunctions.clamp(newX), GridFunctions.clamp(newY));
		
			return newPoint;
		}
		else return fireLocation;
	}
	
	public String toString(){
		return "Checking the weather";
	}
}
