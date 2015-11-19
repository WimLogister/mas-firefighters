package firefighters.pathfinding;

import java.util.HashSet;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import search.AstarSearch;
import search.Path;
import firefighters.utils.Directions;

public class PathFindingTest {

  public static void main(String[] args) {
    hashingTest();
    testPathFinding();
  }

  public static void hashingTest() {
    GridPoint point = new GridPoint(5, 5);
    GridPoint copy = new GridPoint(5, 5);

    assert point.equals(copy);

    GridState state = new GridState(point);
    GridState identical = new GridState(copy);

    HashSet<GridState> explored = new HashSet<>();
    explored.add(state);

    assert explored.contains(identical);
    System.out.println("PASSED");
  }

  public static void testPathFinding() {
    Context<Object> context = new DefaultContext<>();

    GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);

    int gridSize = 20;
    GridBuilderParameters<Object> gridBuilderParameters = new GridBuilderParameters<>(new WrapAroundBorders(),
                                                                                      new SimpleGridAdder<Object>(),
                                                                                      true,
                                                                                      gridSize,
                                                                                      gridSize);
    Grid<Object> grid = gridFactory.createGrid("grid", context, gridBuilderParameters);
    
    GridPoint source = new GridPoint(5, 5);
    GridPoint target = new GridPoint(9, 9);
    
    AstarSearch<GridState, GridAction, GridSuccessorFunction, GridManhattanHeuristic, GridPointGoalTest> aStar;
    aStar = new AstarSearch<>(new GridSuccessorFunction(grid),
                              new GridManhattanHeuristic(target),
                              new GridPointGoalTest(target));
    
    Path<GridState, GridAction> path = aStar.findPath(new GridState(source));
    assert path.getCost() == 4;
    for (GridAction action : path.getRoute()) {
      assert action.getDirection() == Directions.NORTH_EAST;
    }
    System.out.println("Expanded " + aStar.getNodesExpanded());
  }

}
