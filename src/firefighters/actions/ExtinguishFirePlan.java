package firefighters.actions;

import java.util.List;

import lombok.Getter;
import repast.simphony.space.grid.GridPoint;

public class ExtinguishFirePlan
    extends Plan {

  @Getter
  private GridPoint fireLocation;

  public ExtinguishFirePlan(List<AbstractAction> steps, GridPoint fireLocation) {
    super(steps);
    this.fireLocation = fireLocation;
  }

}
