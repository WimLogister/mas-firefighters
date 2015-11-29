package firefighters.utility;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import constants.SimulationConstants;
import constants.SimulationParameters;
import firefighters.actions.AbstractAction;
import firefighters.actions.Extinguish;
import firefighters.actions.Plan;
import firefighters.agent.Agent;
import firefighters.utils.Directions;
import firefighters.world.Fire;
import static firefighters.utils.GridFunctions.getCellNeighborhood;

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

public class ComponentsUtilityFunction extends DiscountedUtilityFunction{
	
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
	private CheckWeatherUtilityFunction weatherFunction;
	
	public ComponentsUtilityFunction(double weightRisk, double weightCooperating, Grid grid){
		this.weightRisk = weightRisk;
		this.weightCooperating = weightCooperating;
		this.grid = grid;
		this.fixedFunction = new ExpectedBountiesUtilityFunction();
		this.riskFunction = new RiskTakingUtilityFunction(grid);
		this.cooperativeFunction = new CooperativeUtilityFunction(grid);
		this.weatherFunction = new CheckWeatherUtilityFunction();
	}
	
	@Override
	public double calculateUtility(AbstractAction action) {
		return fixedFunction.calculateUtility(action) + weightRisk * riskFunction.calculateUtility(action) + weightCooperating * cooperativeFunction.calculateUtility(action) + weatherFunction.calculateUtility(action);
	}
}