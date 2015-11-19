package firefighters.pathfinding;

import lombok.AllArgsConstructor;
import repast.simphony.space.grid.GridPoint;
import search.GoalTest;

/** Implementation of {@link GoalTest} used for pathfinding in a grid */
@AllArgsConstructor
public class GridPointGoalTest
    extends GoalTest<GridState> {

  private GridPoint targetPoint;

  @Override
  public boolean check(GridState state) {
    return state.getPosition().equals(targetPoint);
  }

}
