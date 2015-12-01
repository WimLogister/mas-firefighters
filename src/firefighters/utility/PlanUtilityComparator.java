package firefighters.utility;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import firefighters.actions.Plan;
import firefighters.agent.Agent;

/** Compares plans according to their utility */
@AllArgsConstructor
public class PlanUtilityComparator
    implements Comparator<Plan> {

  private UtilityFunction utilityFunction;

  private Agent agent;

  @Override
  public int compare(Plan planA, Plan planB) {
    double utilityDifference = utilityFunction.calculateUtility(planA, agent)
                               - utilityFunction.calculateUtility(planB, agent);
    return (int) Math.signum(utilityDifference);
  }

}
