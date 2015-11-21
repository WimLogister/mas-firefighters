package firefighters.actions;

import static firefighters.utils.GridFunctions.findShortestPath;
import static firefighters.utils.GridFunctions.getNeighboringPoint;
import static firefighters.utils.GridFunctions.getRandomNeighboringPoint;
import static firefighters.utils.GridFunctions.isInFrontOfAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import search.Path;
import firefighters.agent.Agent;
import firefighters.pathfinding.GridAction;
import firefighters.pathfinding.GridState;
import firefighters.utility.PlanUtilityComparator;
import firefighters.utility.UtilityFunction;
import firefighters.utils.Directions;
import firefighters.world.Fire;

@AllArgsConstructor
public class Planner {
  
  private UtilityFunction utilityFunction;

  /** Returns a plan for the agent */
  public Plan devisePlan(Agent agent) {
    List<Plan> possiblePlans = discoverPossiblePlans(agent);
    return Collections.max(possiblePlans, new PlanUtilityComparator(utilityFunction));
  }

  // TODO Check if the square the agent is on is on fire
  private List<Plan> discoverPossiblePlans(Agent agent) {
    Grid<?> grid = agent.getGrid();
    GridPoint agentPosition = grid.getLocation(agent);

    List<Plan> possiblePlans = new ArrayList<>();
    List<GridCell<Fire>> fireCells = agent.getKnownFireLocations();
    // System.out.println("fire cells: " + fireCells.size());
    for (GridCell<Fire> fireCell : fireCells) {
      GridPoint firePoint = fireCell.getPoint();
      Path<GridState, GridAction> path = findShortestPath(grid, agentPosition, firePoint);
      if (path != null && path.isValidPath()) {
        // System.out.println("ag " + agentPosition + " fire  " + firePoint);
        List<AbstractAction> actions = convertToPrimitiveActions(path, agent.getDirection());
        actions.add(new Extinguish(firePoint));
        Plan plan = new Plan(actions);
        possiblePlans.add(plan);
      }
    }
    if (possiblePlans.size() == 0) {
      // Move randomly
      List<AbstractAction> actions = new ArrayList<>();
      MoveAndTurn move;
      GridPoint randomPoint = getRandomNeighboringPoint(grid, agentPosition);
      if (randomPoint == null)
        move = new MoveAndTurn(Directions.getRandomDirection());
      else
        move = new MoveAndTurn(randomPoint, Directions.getRandomDirection());
      actions.add(move);
      possiblePlans.add(new Plan(actions));
    }
    return possiblePlans;
  }

  private List<AbstractAction> convertToPrimitiveActions(Path<GridState, GridAction> path, Directions agentDirection) {
    GridPoint agentPosition = path.getStart().getPosition();
    GridPoint firePosition = path.getGoal().getPosition();

    List<AbstractAction> abstractActions = new ArrayList<>();
    List<GridAction> gridActions = path.getRoute();
    if (gridActions.size() == 1) {
      if (!isInFrontOfAgent(agentPosition, agentDirection, firePosition)) {
        Directions desiredDirection = findDirection(agentPosition, firePosition);
        abstractActions.add(new MoveAndTurn(desiredDirection));
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