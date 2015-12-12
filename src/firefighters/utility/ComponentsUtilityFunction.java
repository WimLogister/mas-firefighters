package firefighters.utility;

import repast.simphony.space.grid.Grid;
import firefighters.actions.Plan;
import firefighters.agent.Agent;

/**
 * Utility is determined by setting different weights to the different components of the utility function:
 * Risk taking versus playing safe agents (how dangerous is the fire they try to extinguish, determined by ratio fire:agents in its surrounding)
 * Selfish versus cooperative agents (bounty received for helping other agents)
 * Honest versus lying agents (providing wrong information results in lower/higher utility or less costs for lying/)
 * 
 * Important for all agents:
 * Saving the forest: utility for hosing the fire
 * Bonus for really extinguishing it? 
 */

public class ComponentsUtilityFunction
    implements UtilityFunction {
	
	// Weight for different components of the utility function,
	// the higher the weight the more the particular agent is tended to act ...
	// ... with risk
	private double weightRisk;
	// ... cooperating
	private double weightCooperating;
	// ... lying
	//private double weightLying;
	private Grid<Object> grid;
	
	private ExpectedBountiesUtilityFunction fixedFunction;
	private RiskTakingUtilityFunction riskFunction;
	private CooperativeUtilityFunction cooperativeFunction;
	
	public ComponentsUtilityFunction(double weightRisk, double weightCooperating, Grid grid){
		this.weightRisk = weightRisk;
		this.weightCooperating = weightCooperating;
		this.grid = grid;
		this.fixedFunction = new ExpectedBountiesUtilityFunction();
		this.riskFunction = new RiskTakingUtilityFunction(grid);
		this.cooperativeFunction = new CooperativeUtilityFunction(grid);
	}
	
  @Override
  public double calculateUtility(Plan plan, Agent agent) {
    return fixedFunction.calculateUtility(plan, agent) + weightRisk * riskFunction.calculateUtility(plan, agent)
           + weightCooperating * cooperativeFunction.calculateUtility(plan, agent);
  }
}