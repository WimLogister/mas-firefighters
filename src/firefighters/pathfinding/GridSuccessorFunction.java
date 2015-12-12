package firefighters.pathfinding;

import static firefighters.utils.GridFunctions.getCellNeighborhood;
import static firefighters.utils.GridFunctions.getNeighboringPoint;
import static firefighters.utils.GridFunctions.isLegal;
import static firefighters.utils.GridFunctions.isOnFire;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import communication.information.InformationType;
import communication.information.WeatherInformation;
import constants.SimulationConstants;
import lombok.AllArgsConstructor;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import search.ImmutableTriple;
import search.SuccessorFunction;
import firefighters.agent.Agent;
import firefighters.utils.Directions;
import firefighters.world.Fire;
import firefighters.world.Rain;
import firefighters.world.Wind;

/**
 * Implementation of {@link SuccessorFunction} used for pathfinding in a grid with 8-directional movement. Grid points
 * which are on fire are considered impassable
 */
@AllArgsConstructor
public class GridSuccessorFunction
    extends SuccessorFunction<GridState, GridAction> {
 
  private Agent agent;
  private Grid<?> grid;
  private GridPoint target;

  @Override
  public List<ImmutableTriple<GridState, GridAction, Double>> apply(GridState state) {
	    List<ImmutableTriple<GridState, GridAction, Double>> successors = new ArrayList<>();
	    GridPoint currentPosition = state.getPosition();
	    for (Directions direction : Directions.values()) {
	      if (isLegal(currentPosition, direction)) {
	        GridPoint neighboringPoint = getNeighboringPoint(currentPosition, direction);
	        if (neighboringPoint.equals(target) || !isOnFire(grid, neighboringPoint)) {
	          GridState np = new GridState(neighboringPoint);
	          ImmutableTriple<GridState, GridAction, Double> successor = ImmutableTriple.of(np,
	                                                                                        new GridAction(direction),
	                                                                                        calculateCost(np));
	          successors.add(successor);
	        }
	      }
	    }
	    return successors;
	  }
  
  /**
   * If agent has weather information stored, this method calculates the costs of the gridstate
   * given the wind and rain information and the surrounding fires.
   * If wind is blowing fire into this direction the cost is higher.
   * Rain might lower the cost again.
   * Cost is a double in range [1,4] where 4 denotes the highest amount of danger 
   * (cost = 4 if fires from 3 directions are spreading to the newPoint with maximum probability)
   */
  public double calculateCost(GridState newPoint){
	  // Does the agent have access to weather information?
	  return agent.danger(newPoint.getPosition());
	  
	  
	 /* if(agent.hasWeatherInfo()) {
		  // Check fire speed, check wind speed, calculate new fire speed, see if it is likely to spread to the state its gridpoint
		  // Check if it is raining, because then it is slowing down the rain again
		  return danger(newPoint);
	  }
	  else return 1.0;*/
  }
  
  // Method is called when agent has weather information
 /* private double danger(GridState newPoint){
	GridPoint toCheck = newPoint.getPosition();
	// See if the wind is blowing the fire in the agent's direction
	WeatherInformation weatherInfo = (WeatherInformation) agent.getInformationStore().getLatestInformationOfType(InformationType.WeatherInformation);
	
	// Check if there is rain in the gridcell
	boolean rainInNewPoint = false;
	if(!getCellNeighborhood(grid,toCheck,Rain.class,0,true).isEmpty()) rainInNewPoint = true;
	
	double danger = 1;
	// Loop through all the neighboring firecells
	for (GridCell<Fire> fireCell : getCellNeighborhood(grid, toCheck, Fire.class, 1, false)) {
		GridPoint pt = fireCell.getPoint();
		int fireX = pt.getX();
		int fireY = pt.getY();
		Vector2 fireVelocity = null;
		boolean rainInFireLoc = false;
		for (Object obj : grid.getObjectsAt(fireX, fireY)){
			if (obj instanceof Fire) fireVelocity = ((Fire) obj).getVelocity();
			else if (obj instanceof Rain) rainInFireLoc = true;
		}	
		
		Vector2 windVelocity = weatherInfo.getWind();
		// Influence of wind on fire directions  
		if(!fireVelocity.equals(null)){
			fireVelocity.add(windVelocity).clamp(0, SimulationConstants.MAX_FIRE_SPEED);
			if(rainInFireLoc) {
				float newSpeed = fireVelocity.len() - SimulationConstants.MAX_FIRE_SPEED * 0.4f;
				fireVelocity.setLength(newSpeed);
				fireVelocity.clamp(0, SimulationConstants.MAX_FIRE_SPEED);
			}
		}
		else {
			throw new IllegalArgumentException("This is not supposed to happen! In GridSuccessorFunction.java");
		}
		// Determine if the updated fire direction is towards newPoint
		Directions dir = Directions.fromAngleToDir(fireVelocity.angle());
		boolean headingTowardsNewPoint = false;
		if(fireX + dir.xDiff == toCheck.getX() && fireY + dir.yDiff == toCheck.getY()) headingTowardsNewPoint = true;
		
		if(headingTowardsNewPoint){
			// Has to do with the way the rain is influencing the speed of the fire in Fire.java
			if(!rainInFireLoc && rainInNewPoint){
				float newSpeed = fireVelocity.len() - SimulationConstants.MAX_FIRE_SPEED * 0.2f;
				fireVelocity.setLength(newSpeed);
				fireVelocity.clamp(0, SimulationConstants.MAX_FIRE_SPEED);
			}
			// Value between 0 and 1
			double spreadChange = fireVelocity.len()*(1/SimulationConstants.MAX_FIRE_SPEED);
			danger = danger + spreadChange;
		}
	}
	if(danger<0||danger>4) 
		throw new IllegalArgumentException("Calculated cost is out of range!");
	return danger;
  }*/

}
