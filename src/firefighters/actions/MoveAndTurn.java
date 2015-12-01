package firefighters.actions;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import firefighters.agent.Agent;
import firefighters.utils.Directions;
import firefighters.utils.GridFunctions;
import firefighters.world.Fire;

/** Action to define an agent's (optionally) moving to a neighboring cell */
@AllArgsConstructor
public class MoveAndTurn implements PrimitiveAction {

  @Getter
  private GridPoint newPos;
  @NonNull
  private Directions newDir;

	/**
	 * Move the agent to the indicated new position in the grid and turn
	 * agent in indicated direction.
	 */
	@Override
  public void execute(Agent agent) {
    if (newPos != null)
      agent.move(newPos);
		agent.turn(newDir);
	}

	@Override
  public boolean checkPreconditions(Agent agent) {
    // TODO Can 2 agents occupy the same square?
    Grid<Object> grid = agent.getGrid();
    List<GridCell<Fire>> fireCells = GridFunctions.getCellNeighborhood(grid, newPos, Fire.class, 0, true);
    if (fireCells.size() > 0)
      return false;
    GridPoint currentPosition = grid.getLocation(agent);
    return GridFunctions.areAdjacent(currentPosition , newPos);
  }
	
	public String toString(){
		return "Move and turn";
	}


}
