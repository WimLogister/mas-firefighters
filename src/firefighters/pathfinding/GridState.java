package firefighters.pathfinding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import repast.simphony.space.grid.GridPoint;
import search.SearchState;

/** Implementation of {@link SearchState} used for pathfinding in a grid */
@AllArgsConstructor
public class GridState
    extends SearchState {

  @Getter
  private GridPoint position;

  @Override
  public int hashCode() {
    return position.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GridState other = (GridState) obj;
    if (position == null) {
      if (other.position != null)
        return false;
    } else if (!position.equals(other.position))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "GridState [position=" + position + "]";
  }

}
