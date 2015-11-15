package firefighters.actions;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import firefighters.agent.Agent;
import firefighters.utils.Directions;
import repast.simphony.space.grid.GridPoint;

@RequiredArgsConstructor
public class AgentMove implements PrimitiveAction {

	@NonNull private GridPoint newPos;
	@NonNull private Agent agent;
	@NonNull private Directions newDir;
	
	/**
	 * Move the agent to the indicated new position in the grid and turn
	 * agent in indicated direction.
	 */
	@Override
	public void execute() {
		agent.move(newPos);
		agent.turn(newDir);
	}

	@Override
	public boolean checkPreconditions() {
		// newPos may not be occupied if the agent is to move to it
		if (agent.getGrid().getObjectsAt(newPos.getX(), newPos.getY()).iterator().hasNext())
			return false; else return true;
	}

}
