package firefighters.utility;

import constants.SimulationConstants;
import firefighters.actions.AbstractAction;
import firefighters.actions.Extinguish;

/** Utility function that has a positive utility only for hosing down a fire */
public class ExpectedBountiesUtilityFunction
    extends DiscountedUtilityFunction {

  @Override
  public double calculateUtility(AbstractAction action) {
    if (action instanceof Extinguish)
      return SimulationConstants.BOUNTY_PER_FIRE_EXTINGUISHED;
    else
      return 0;
  }

}
