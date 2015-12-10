package firefighters.pathfinding;

import lombok.AllArgsConstructor;
import repast.simphony.space.grid.GridPoint;
import search.HeuristicFunction;
import firefighters.utils.Metrics;

/** Implementation of {@link HeuristicFunction} used for pathfinding in a grid with diagonal movement */
@AllArgsConstructor
public class GridHammingDistanceHeuristic
    extends HeuristicFunction<GridState> {

  private GridPoint targetPoint;

  @Override
  public double evaluate(GridState state) {
    return Metrics.hammingDistance(targetPoint, state.getPosition());
  }

}
