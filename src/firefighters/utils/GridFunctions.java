package firefighters.utils;

import static constants.SimulationConstants.MAX_SEARCH_DISTANCE;
import static constants.SimulationConstants.RANDOM;
import static constants.SimulationParameters.gridSize;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNeighborhood2d;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import search.AstarSearch;
import search.ImmutableTriple;
import search.Path;
import firefighters.agent.Agent;
import firefighters.pathfinding.GridAction;
import firefighters.pathfinding.GridHammingDistanceHeuristic;
import firefighters.pathfinding.GridPointGoalTest;
import firefighters.pathfinding.GridState;
import firefighters.pathfinding.GridSuccessorFunction;
import firefighters.world.Fire;

public class GridFunctions {

  /** Returns whether it is legal to move in the specified direction from the given point */
  public static boolean isLegal(GridPoint point, Directions direction) {
    return isWithinBounds(point.getX() + direction.xDiff, point.getY() + direction.yDiff);
  }

  /** Returns whether the provided x and y coordinates are inside the grid */
  public static boolean isWithinBounds(int xCoord, int yCoord) {
    return xCoord > 0 && xCoord < gridSize && yCoord > 0 && yCoord < gridSize;
  }

  /**
   * Returns the point reached moving in the specified direction from the given source point. Performs no bounds
   * checking
   */
  public static GridPoint getNeighboringPoint(GridPoint sourcePoint, Directions direction) {
    return new GridPoint(sourcePoint.getX() + direction.xDiff, sourcePoint.getY() + direction.yDiff);
  }

  /**
   * Returns whether the given gridpoints are adjacent with 8-directional movement. Returns true if both arguments are
   * the same
   */
  public static boolean areAdjacent(GridPoint pointA, GridPoint pointB) {
    return Metrics.manhattanDistance(pointA, pointB) <= 1;
  }

  /**
   * Check whether the parameter object position is either directly or diagonally in front of the parameter agent
   * position, given the agent's direction
   * 
   * @param agentPos
   * @param agentDir
   * @param objPos
   * @return
   */
  public static boolean isInFrontOfAgent(GridPoint agentPos, Directions direction, GridPoint objPos) {
    // TODO Test
    return agentPos.getX() + direction.xDiff == objPos.getX() || agentPos.getY() + direction.yDiff == objPos.getY();
  }

  /** Returns whether there is a fire in given grid point */
  public static boolean isOnFire(Grid<?> grid, GridPoint point) {
    List<GridCell<Fire>> fireCells = GridFunctions.getCellNeighborhood(grid, point, Fire.class, 0, true);
    return fireCells.size() > 0;
  }

  /**
   * Returns a list of cells matching a given type in the neighborhood of the specified point
   * 
   * @param grid
   * @param point
   *        The center of the neighborhood
   * @param aClass
   *        The type of cell to be found
   * @param radius
   *        The extent of the region, same for both axis
   * @param includeCenter
   *        Whether the central point should be part of the returned neighborhood
   * @return
   */
  public static <T> List<GridCell<T>> getCellNeighborhood(Grid<?> grid,
                                                          GridPoint point,
                                                          Class<T> aClass,
                                                          int radius,
                                                          boolean includeCenter) {
    return getCellNeighborhood(grid, point, aClass, radius, radius, includeCenter);
  }

  /**
   * Returns a list of cells matching a given type in the neighborhood of a specified point
   * 
   * @param grid
   * @param point
   *        The center of the neighborhood
   * @param aClass
   *        The type of cell to be found
   * @param xExtent
   *        The extent of the region in the x-axis
   * @param yExtent
   *        The extent of the region in the y-axis
   * @param includeCenter
   *        Whether the central point itself should be part of the returned neighborhood
   * @return
   */
  public static <T> List<GridCell<T>> getCellNeighborhood(Grid<?> grid,
                                                          GridPoint point,
                                                          Class<T> aClass,
                                                          int xExtent,
                                                          int yExtent,
                                                          boolean includeCenter) {
    GridCellNeighborhood2d<T> ngh = new GridCellNeighborhood2d<>(grid, point, aClass, xExtent, yExtent, false);
    List<GridCell<T>> gridCells = ngh.getNeighborhood(includeCenter);
    return gridCells;
  }

  /** Returns the shortest path from the source to the target point */
  public static Path<GridState, GridAction> findShortestPath(Agent agent, Grid<?> grid, GridPoint source, GridPoint target) {
    AstarSearch<GridState, GridAction, GridSuccessorFunction, GridHammingDistanceHeuristic, GridPointGoalTest> aStar = createAstarPathFinder(agent, grid,
                                                                                                                                       target);
    Path<GridState, GridAction> shortestPath = aStar.findPath(new GridState(source));
    return shortestPath;
  }

  /** Returns an instance of AstarSearch to search for a path to the specified target point */
  public static AstarSearch<GridState, GridAction, GridSuccessorFunction, GridHammingDistanceHeuristic, GridPointGoalTest>
      createAstarPathFinder(Agent agent, Grid<?> grid, GridPoint target) {
    return new AstarSearch<>(agent,
    						 new GridSuccessorFunction(agent,grid, target),
                             new GridHammingDistanceHeuristic(target),
                             new GridPointGoalTest(target),
                             MAX_SEARCH_DISTANCE);
  }

  public static List<GridPoint> getNeighboringPoints(Agent agent, Grid<?> grid, GridPoint point) {
    GridSuccessorFunction successorFunction = new GridSuccessorFunction(agent, grid, null);
    List<ImmutableTriple<GridState, GridAction, Double>> successors = successorFunction.apply(new GridState(point));
    List<GridPoint> neighboringPoints = new ArrayList<>();
    for (ImmutableTriple<GridState, GridAction, Double> successor : successors) {
      GridPoint position = successor.getLeft().getPosition();
      neighboringPoints.add(position);
    }
    return neighboringPoints;
  }

  /**
   * Returns a random point at a distance one from the given point, or null if it is not legal to move to any
   * neighboring square
   */
  public static GridPoint getRandomNeighboringPoint(Agent agent, Grid<?> grid, GridPoint point) {
    List<GridPoint> neighboringPoints = getNeighboringPoints(agent, grid, point);
    if (neighboringPoints.isEmpty())
      return null;
    int rand = RANDOM.nextInt(neighboringPoints.size());
    return neighboringPoints.get(rand);
  }
}
