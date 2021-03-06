package firefighters.actions;

import static firefighters.utils.GridFunctions.isInFrontOfAgent;
import static firefighters.utils.GridFunctions.isOnFire;
import lombok.AllArgsConstructor;
import lombok.Getter;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import firefighters.agent.Agent;

@AllArgsConstructor
public class Extinguish implements PrimitiveAction {

	@Getter 
	private GridPoint firePosition;

	@Override
  public void execute(Agent agent) {
    agent.hose(firePosition);
	}
	
	@Override
  public boolean checkPreconditions(Agent agent) {
    Grid<Object> grid = agent.getGrid();
    GridPoint agentPosition = grid.getLocation(agent);
    return isOnFire(grid, firePosition) && isInFrontOfAgent(agentPosition, agent.getDirection(), firePosition);
	}

}
