package firefighters.utility;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import firefighters.actions.Plan;

/** Compares plans according to their utility */
@AllArgsConstructor
public class PlanUtilityComparator
    implements Comparator<Plan> {

  private UtilityFunction utilityFunction;

  @Override
  public int compare(Plan planA, Plan planB) {
    double utilityDifference = utilityFunction.calculateUtility(planA) - utilityFunction.calculateUtility(planB);
    return (int) Math.signum(utilityDifference);
  }

}
