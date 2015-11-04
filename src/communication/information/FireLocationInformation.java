package communication.information;

import lombok.Getter;
import repast.simphony.space.grid.GridPoint;

/** Element of information describing the location of a square that is on fire */
@Getter
public class FireLocationInformation
    extends InformationPiece {

  /** x and y coordinate in the grid of the square that is on fire */
  private GridPoint position;

  public FireLocationInformation(int x, int y) {
    super(InformationType.FireLocation);
    position = new GridPoint(x, y);
  }
}
