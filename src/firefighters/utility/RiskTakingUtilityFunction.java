package firefighters.utility;

import static firefighters.utils.GridFunctions.getCellNeighborhood;

import java.util.List;

import lombok.AllArgsConstructor;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import constants.SimulationConstants;
import constants.SimulationParameters;
import firefighters.actions.AbstractAction;
import firefighters.actions.Extinguish;
import firefighters.agent.Agent;
import firefighters.world.Fire;

@AllArgsConstructor
public class RiskTakingUtilityFunction extends DiscountedUtilityFunction {
	
	private Grid<Object> grid;

	@Override
	public double calculateUtility(AbstractAction action) {
		double utility = 0;
		if (action instanceof Extinguish) {
			GridPoint gpFire = ((Extinguish) action).getFirePosition();
			
			// Calculate ratio fire:agent in region defined by the perception range of the agent
			List<GridCell<Fire>> fires = getCellNeighborhood(grid, gpFire, Fire.class, SimulationParameters.perceptionRange,true);
			List<GridCell<Agent>> agents = getCellNeighborhood(grid, gpFire, Agent.class, SimulationParameters.perceptionRange,true);
			double fireSize = (double) fires.size();
			double agentSize = (double) agents.size();
			double ratio = fireSize / (agentSize+1);
			utility = ratio * 100;
		}	
		
		// Bonus for extinguishing big fires?
		return utility;
	}

	
}
