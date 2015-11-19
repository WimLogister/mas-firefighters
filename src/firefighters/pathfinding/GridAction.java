package firefighters.pathfinding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import search.SearchAction;
import firefighters.utils.Directions;

/** Implementation of {@link SearchAction} used for pathfinding in a grid with 8-directional movement */
@AllArgsConstructor
public class GridAction
    implements SearchAction<GridState> {

  /** The direction of movement */
  @Getter
  private Directions direction;

}
