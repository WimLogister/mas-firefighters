package firefighters.actions;

import static firefighters.utils.GridFunctions.findShortestPath;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import firefighters.agent.Agent;
import lombok.Getter;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import search.Path;
import firefighters.actions.CheckWeather;
import firefighters.information.WeatherInformation;
import firefighters.pathfinding.GridAction;
import firefighters.pathfinding.GridState;
import firefighters.utils.Directions;
import firefighters.utils.GridFunctions;
import firefighters.actions.Planner;
import firefighters.actions.ExtinguishFirePlan;

public class CheckWeatherExtinguishFirePlan extends Plan{
	
	@Getter
	private GridPoint actualFireLocation;
	private GridPoint predictedFireLocation;
	// Shortest path to the actual fire
	private Path<GridState,GridAction> pathToFire;
	private Agent agent;
	
	/**
	 * At first, the predicted fire location is equal to the real fire location, will be changed
	 * if the step of checking the weather is executed
	 */
	public CheckWeatherExtinguishFirePlan(List<AbstractAction> steps, GridPoint actualFireLocation, Path<GridState,GridAction> pathToFire, Agent agent) {
	    super(steps);
	    // Add the action of checking the weather at the beginning
	    steps.add(0, new CheckWeather());
	    this.actualFireLocation = actualFireLocation;
	    // Has to be calculated
	    this.predictedFireLocation = null;
	    this.pathToFire = pathToFire;
	    this.agent = agent;
	}

	/**
	 * If the action is of type CheckWeather, the predicted fire location is updated
	 * according to calculation of the new fire position according to the current wind
	 */
	@Override
	public void executeNextStep(Agent agent) {
		if(getSteps().get(0) instanceof CheckWeather){
			System.out.println("Checking the weather");
			getSteps().remove(0).execute(agent);
			WeatherInformation weather = CheckWeather.getWeather();
			predictedFireLocation = calculateNewFirePosition(weather);
			// Changed the fire position, now calculate new path to this new location
			Grid<?> grid = agent.getGrid();
			GridPoint agentPosition = grid.getLocation(agent);
			Path<GridState, GridAction> pathToNewLoc = findShortestPath(grid, agentPosition, predictedFireLocation);
			List<AbstractAction> actions = Planner.convertToPrimitiveActions(pathToNewLoc, agent.getDirection());
			Plan newPlan = new ExtinguishFirePlan(actions, predictedFireLocation);
			agent.setCurrentPlan(newPlan);
		}
		else getSteps().remove(0).execute(agent);	    
	}
	
	public GridPoint calculateNewFirePosition(WeatherInformation weather){
		Vector2 wind = weather.getWind();
		// Get shortest path to current fire position to know how many steps we need to take into consideration
		int noOfSteps = pathToFire.getRoute().size();	     
		
		if(noOfSteps>0){
			// Calculate where this fire will be assuming that the wind stays the same given the number of steps it 
			// takes for the firefighter to be there
			Directions dir = Directions.fromVectorToDir(wind);
			int xDiff = dir.xDiff*noOfSteps;
			int yDiff = dir.yDiff*noOfSteps;
		
			System.out.println("Real fire location: " + actualFireLocation.getX() + "," + actualFireLocation.getY());
			int newX = actualFireLocation.getX() + xDiff;
			int newY = actualFireLocation.getY() + yDiff;
			System.out.println("Predicted fire location: " + newX + "," + newY);
			
			GridPoint newPoint = new GridPoint(GridFunctions.clamp(newX), GridFunctions.clamp(newY));
		
			return newPoint;
		}
		else return predictedFireLocation;
	}
}
