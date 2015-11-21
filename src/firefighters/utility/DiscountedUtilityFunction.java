package firefighters.utility;

import firefighters.actions.AbstractAction;
import firefighters.actions.Plan;

/** Utility implementation based on primitive actions, using a discount factor */
public abstract class DiscountedUtilityFunction
    implements UtilityFunction {

  /** The discount factor */
  private double alpha;

  public abstract double calculateUtility(AbstractAction action);

  @Override
  public double calculateUtility(Plan plan) {
    double planUtility = 0;
    double discountFactor = 1;
    for (AbstractAction action : plan.getSteps()) {
      planUtility = calculateUtility(action) * discountFactor;
      discountFactor *= alpha;
    }
    return planUtility;
  }

}