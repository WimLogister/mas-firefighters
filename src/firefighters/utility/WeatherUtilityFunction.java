package firefighters.utility;

import static firefighters.utils.GridFunctions.getCellNeighborhood;

import java.util.List;

import lombok.AllArgsConstructor;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import constants.SimulationParameters;
import firefighters.actions.AbstractAction;
import firefighters.actions.CheckWeather;
import firefighters.actions.Extinguish;
import firefighters.agent.Agent;
import firefighters.world.Fire;

@AllArgsConstructor
public class WeatherUtilityFunction extends DiscountedUtilityFunction{
	
	private Grid<Object> grid;

	@Override
	public double calculateUtility(AbstractAction action, Agent agent) {
		double result = 0;
		
		if (action instanceof CheckWeather) {
			// Bonus for checking the weather if an agent haven't done this for a while
			int currentTick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount(); 
			int timeNotChecked = currentTick - agent.getTickWeatherLastChecked();
			double bonusNotChecked = 0;
			if(timeNotChecked > 0 && timeNotChecked <= 5) bonusNotChecked = 50;
			else if(timeNotChecked > 5 && timeNotChecked <= 10) bonusNotChecked = 100;
			else if(timeNotChecked > 10 && timeNotChecked <= 25 ) bonusNotChecked = 200;
			else if(timeNotChecked > 25) bonusNotChecked = 500;
			result = result + bonusNotChecked;
			
			// Calculate ratio fire:agent in region defined by the perception range of the agent
			GridPoint gpAgent = agent.getAgentPosition();
			List<GridCell<Fire>> fires = getCellNeighborhood(grid, gpAgent, Fire.class, SimulationParameters.perceptionRange,true);
			List<GridCell<Agent>> agents = getCellNeighborhood(grid, gpAgent, Agent.class, SimulationParameters.perceptionRange,true);
			double fireSize = (double) fires.size();
			double agentSize = (double) agents.size();
			double ratio = fireSize / (agentSize+1);
			double bonusDanger = ratio * 25;
			//result = result + bonusDanger;
		}
		return result;
	}

}
