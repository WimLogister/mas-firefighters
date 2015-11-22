package firefighters.pathfinding;

import static firefighters.utils.GridFunctions.getNeighboringPoint;
import static firefighters.utils.GridFunctions.isLegal;
import static firefighters.utils.GridFunctions.isOnFire;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import search.ImmutableTriple;
import search.SuccessorFunction;
import firefighters.utils.Directions;

/**
 * Implementation of {@link SuccessorFunction} used for pathfinding in a grid with 8-directional movement. Grid points
 * which are on fire are considered impassable
 */
@AllArgsConstructor
public class GridSuccessorFunction
    extends SuccessorFunction<GridState, GridAction> {

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
          ImmutableTriple<GridState, GridAction, Double> successor = ImmutableTriple.of(new GridState(neighboringPoint),
                                                                                        new GridAction(direction),
                                                                                        1.0);
          successors.add(successor);
        }
      }
    }
    return successors;
  }

}
