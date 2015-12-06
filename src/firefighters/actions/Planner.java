package firefighters.actions;

import static firefighters.utils.GridFunctions.findShortestPath;
import static firefighters.utils.GridFunctions.getCellNeighborhood;
import static firefighters.utils.GridFunctions.getNeighboringPoint;
import static firefighters.utils.GridFunctions.getNeighboringPoints;
import static firefighters.utils.GridFunctions.getRandomNeighboringPoint;
import static firefighters.utils.GridFunctions.isInFrontOfAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.jogamp.opengl.math.VectorUtil.Winding;

import lombok.AllArgsConstructor;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import search.Path;
import communication.information.FireLocationInformation;
import communication.information.InformationType;
import communication.information.WeatherInformation;
import firefighters.agent.Agent;
import firefighters.pathfinding.GridAction;
import firefighters.pathfinding.GridState;
import firefighters.utility.PlanUtilityComparator;
import firefighters.utility.UtilityFunction;
import firefighters.utils.Directions;
import firefighters.world.Fire;
import communication.information.InformationType;

@AllArgsConstructor
public class Planner {
  
  private UtilityFunction utilityFunction;

  /** Returns a plan for the agent */
  public Plan devisePlan(Agent agent) {
    List<Plan> possiblePlans = discoverPossiblePlans(agent);
    return Collections.max(possiblePlans, new PlanUtilityComparator(utilityFunction, agent));
  }

  // TODO Check if the square the agent is on is on fire
  private List<Plan> discoverPossiblePlans(Agent agent) {
    Grid<?> grid = agent.getGrid();
    GridPoint agentPosition = grid.getLocation(agent);

    List<Plan> possiblePlans = new ArrayList<>();
    List<FireLocationInformation> fireCells = agent.getKnownFireLocations();

    if (isAgentCellOnFire(grid, agentPosition)) {
      return deviseEmergencyPlans(grid, agentPosition);
    }

    for (FireLocationInformation fireInformation : fireCells) {
      GridPoint firePoint = fireInformation.getPosition();
      Path<GridState, GridAction> path = findShortestPath(grid, agentPosition, firePoint);
      if (path != null && path.isValidPath()) {
        // System.out.println("ag " + agentPosition + " fire  " + firePoint);
        List<AbstractAction> actions = convertToPrimitiveActions(path, agent.getDirection());
        actions.add(new Extinguish(firePoint));
        Plan plan = new ExtinguishFirePlan(actions, firePoint);
        possiblePlans.add(plan);
      }
    }
    if (possiblePlans.size() == 0) {
      // Move randomly
      Plan randomPlan = deviseRandomPlan(grid, agentPosition);
      possiblePlans.add(randomPlan);	
    } else {
    	// Logger.println("Found plan");
    }
    List<AbstractAction> steps = new ArrayList<AbstractAction>();
    steps.add(new CheckWeather());
    Plan planWeather = new CheckWeatherPlan(steps);
    possiblePlans.add(planWeather);
  
    return possiblePlans;
  }

  /** Called when the agent is on a burning cell */
  private List<Plan> deviseEmergencyPlans(Grid<?> grid, GridPoint agentPosition) {
    List<Plan> possiblePlans = new ArrayList<>();
    for (GridPoint point : getNeighboringPoints(grid, agentPosition)) {
      int xDiff = point.getX() - agentPosition.getX();
      int yDiff = point.getY() - agentPosition.getY();
      Directions facingFire = Directions.findDirection(xDiff, yDiff);
      List<AbstractAction> actions = new ArrayList<>();
      actions.add(new MoveAndTurn(point, facingFire));
      Plan plan = new Plan(actions);
      possiblePlans.add(plan);
    }
    return possiblePlans;
  }

  private boolean isAgentCellOnFire(Grid<?> grid, GridPoint agentPosition) {
    return getCellNeighborhood(grid, agentPosition, Fire.class, 0, true).size() > 0;
  }

  private Plan deviseRandomPlan(Grid<?> grid, GridPoint agentPosition) {
    List<AbstractAction> actions = new ArrayList<>();
    MoveAndTurn move;
    GridPoint randomPoint = getRandomNeighboringPoint(grid, agentPosition);
    if (randomPoint == null)
      move = new MoveAndTurn(agentPosition, Directions.getRandomDirection());
    else
      move = new MoveAndTurn(randomPoint, Directions.getRandomDirection());
    actions.add(move);
    Plan randomPlan = new Plan(actions);
    return randomPlan;
  }

  private List<AbstractAction> convertToPrimitiveActions(Path<GridState, GridAction> path, Directions agentDirection) {
    GridPoint agentPosition = path.getStart().getPosition();
    GridPoint firePosition = path.getGoal().getPosition();

    List<AbstractAction> abstractActions = new ArrayList<>();
    List<GridAction> gridActions = path.getRoute();
    if (gridActions.size() == 0) {
      System.out.println("Planner.convertToPrimitiveActions: Warning: this shouldn't happen");
      // System.out.println(agentPosition + " " + firePosition);
    } else if (gridActions.size() == 1) {
      if (!isInFrontOfAgent(agentPosition, agentDirection, firePosition)) {
        Directions desiredDirection = findDirection(agentPosition, firePosition);
        abstractActions.add(new MoveAndTurn(agentPosition, desiredDirection));
      }
      return abstractActions;
    } else {
      GridPoint currentPt = agentPosition;
      for (int i = 0; i <= gridActions.size() - 3; i++) {
        Directions direction = gridActions.get(i).getDirection();
        MoveAndTurn move = new MoveAndTurn(currentPt, direction);
        abstractActions.add(move);
        currentPt = getNeighboringPoint(currentPt, direction);
      }
      Directions nextToLast = gridActions.get(gridActions.size() - 2).getDirection();
      GridPoint finalPoint = getNeighboringPoint(currentPt, nextToLast);
      Directions last = findDirection(finalPoint, firePosition);
      MoveAndTurn lastMove = new MoveAndTurn(finalPoint, last);
      abstractActions.add(lastMove);
    }
    return abstractActions;
  }

  private Directions findDirection(GridPoint from, GridPoint to) {
    int xDiff = to.getX() - from.getX();
    int yDiff = to.getY() - from.getY();
    return Directions.findDirection(xDiff, yDiff);
  }
}
