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
import firefighters.actions.CheckWeather;
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
	// If weather information is not used, this weight is set to 0, else to 1
	private double weightWeather;
	private Grid<Object> grid;
	
	private ExpectedBountiesUtilityFunction expectedBounties;
	private RiskTakingUtilityFunction riskFunction;
	private CooperativeUtilityFunction cooperativeFunction;
	private WeatherUtilityFunction weatherFunction;
	
	public ComponentsUtilityFunction(double weightRisk, double weightCooperating, Grid grid){
		this.weightRisk = weightRisk;
		this.weightCooperating = weightCooperating;
		this.grid = grid;
		if(SimulationParameters.useWeatherInformation) this.weightWeather = 1;
		else this.weightWeather = 0;
		this.expectedBounties = new ExpectedBountiesUtilityFunction();
		this.riskFunction = new RiskTakingUtilityFunction(grid);
		this.cooperativeFunction = new CooperativeUtilityFunction(grid);
		this.weatherFunction = new WeatherUtilityFunction(grid);
	}
	
	@Override
	public double calculateUtility(AbstractAction action, Agent agent) {
		double utility = expectedBounties.calculateUtility(action, agent) 
				// TODO: finetune check weather utility function
				//+ weightWeather * weatherFunction.calculateUtility(action, agent)
				+ weightRisk * riskFunction.calculateUtility(action, agent) 
				+ weightCooperating * cooperativeFunction.calculateUtility(action, agent);
		//if(action instanceof CheckWeather){
			//System.out.println(agent + ":: " + action + " :: " + utility);
		//}
		return utility;
	}
}