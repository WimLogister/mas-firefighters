package firefighters.utility;

import static constants.SimulationConstants.AGENT_PERCEPTION_DISTANCE;
import static firefighters.utils.GridFunctions.getCellNeighborhood;

import java.util.List;

import lombok.AllArgsConstructor;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

import communication.information.HelpRequestInformation;

import constants.SimulationParameters;
import firefighters.actions.AbstractAction;
import firefighters.actions.Extinguish;
import firefighters.actions.ExtinguishFirePlan;
import firefighters.actions.Plan;
import firefighters.agent.Agent;
import firefighters.utils.Metrics;
import firefighters.world.Fire;

@AllArgsConstructor
public class CooperativeUtilityFunction
    implements UtilityFunction {
	
	private Grid<Object> grid;
	
  /**
   * The utility of helping an agent who has requested help. It's a constant but agents will have a different weight for
   * the CooperativeUtilityFunction
   */
  private static final int helpingUtility = 50;

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
					
			// Working as a team: ideally on every fire one agent, so ratio 1 is ideal
			// With a bigger ratio (so more fires than agents) also some bounty, but not for more than 2
			// times as many fires as agents
			if(ratio > 0.75 && ratio <= 1) utility = utility + 200;
			else if(ratio > 1 && ratio <= 1.5) utility = utility + 100;
			else if(ratio > 1.5 && ratio <= 2) utility = utility + 50;
		}
		
		// Selfish versus cooperative agents, bounty received for helping other agents
		// When are you helping other agents?
		// When you receive a 'help' message and you decide to act on it
				
		// When you communicate 'useful' messages
		// What are useful messages?
		
		return utility;
	}

  @Override
  public double calculateUtility(Plan plan, Agent agent) {
    double utility = 0;
    if (plan instanceof ExtinguishFirePlan) {

      ExtinguishFirePlan extinguishFirePlan = (ExtinguishFirePlan) plan;
      GridPoint fireLocation = extinguishFirePlan.getFireLocation();
      List<HelpRequestInformation> helpRequests = agent.getInformationStore()
                                                       .getInformationOfType(HelpRequestInformation.class);
      for (HelpRequestInformation helpRequest : helpRequests) {
        GridPoint endageredAgentLocation = helpRequest.getAgentLocation();
        if (Metrics.hammingDistance(fireLocation, endageredAgentLocation) < AGENT_PERCEPTION_DISTANCE) {
          utility += helpingUtility;
        }
      }
    }
    return utility;
  }

}
