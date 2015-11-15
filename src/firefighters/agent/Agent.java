package firefighters.agent;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import firefighters.actions.AbstractAction;
import firefighters.utils.Directions;
import firefighters.world.Fire;

/** The only distinction between agents is going to be their Behavior implementation, so this class is final */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Agent {
	
	final Grid<Object> grid;
	final double movementSpeed;
	double money;
	Directions direction;
	Fire targetFire;
	AbstractAction currentAction;
	/** The distance at which the agent can perceive the world around him, i.e. the status of the cells */
	final int perceptionDistance;
	
  public Agent(@NonNull Grid<Object> grid, double movementSpeed, double money, int perceptionDistance) {
    this.grid = grid;
    this.movementSpeed = movementSpeed;
    this.money = money;
    this.direction = Directions.getRandomDirection();
    this.perceptionDistance = perceptionDistance;
  }

  @ScheduledMethod(start = 1, interval = 1)
	public void step() {
		if (checkDeath()) kill();
  }
  
  public void execureCurrentAction() {
	  currentAction.execute();
  }
	
	/**
	 * Check for death condition: being surrounded by fire
	 */
	public boolean checkDeath() {
		// Set up necessary operators
		GridPoint cpt = grid.getLocation(this);
		GridCellNgh<Fire> ngh = new GridCellNgh<>(grid, cpt, Fire.class, 1, 1);
		List<GridCell<Fire>> gridCells = ngh.getNeighborhood(false);

		// Need at least four fires in neighborhood in order to be surrounded
		if (gridCells.size() < 4) return false;

		// If at least four fires in neighborhood, check that they are immediately
		// surrounding the agent, i.e. two fires on same y-axis, two fires on same x-axis
		int enclosedVertical = 0;
		int enclosedHorizontal = 0;
		for (GridCell<Fire> cell : gridCells) {
			for (Fire fire : cell.items()) {
				GridPoint firept = grid.getLocation(fire);
				if (firept.getX() == cpt.getX()) enclosedHorizontal++;
				if (firept.getY() == cpt.getY()) enclosedVertical++;
			}
		}
		return (enclosedVertical >= 2 && enclosedHorizontal >= 2);
	}
	
	/**
	 * Remove this agent from the simulation
	 */
	public void kill() {
		ContextUtils.getContext(this).remove(this);
	}
	
	/**
	 * Move agent to parameter position.
	 * Movement is a stochastic process: each agent's movement speed is modeled as 
	 * the probability of moving to the square it is currently facing.
	 */
	public void move(GridPoint newPt) {
		if (RandomHelper.nextDouble() < movementSpeed) {
      GridPoint pt = grid.getLocation(this);
			/*
			 * Move the agent according to its current direction. How the direction
			 * influences its movement in the grid is modeled by the Directions Enum,
			 * which is used here.
			 */
      // TODO Check if it's legal to move to newPt
			grid.moveTo(this, newPt.getX(), newPt.getY());
		}
	}
	
	public void turn(Directions direction) {
		this.direction = direction;
	}
	
	/**
	 * Extinguish any fires directly or diagonally in front of the agent.
	 */
	public void hose() {
		GridPoint agentPosition = grid.getLocation(this);
		
		final int extinguishRange = 1;
		GridCellNgh<Fire> ngh = new GridCellNgh<>(grid, agentPosition,
				Fire.class, extinguishRange, extinguishRange);
		
		List<Fire> toBeExtinguished = new ArrayList<Fire>();
		
		List<GridCell<Fire>> fireList = ngh.getNeighborhood(false);
		for (GridCell<Fire> cell : fireList) {
			/*
			 * Fires that can be extinguished need to satisfy two conditions:
			 * 1. Have to be within 1 square (already satisfied by extinguishRange
			 * parameter to GridCellNeighborhood).
			 * 2. Agent has to be facing the fires. Use Directions.xdiff and
			 * Directions.ydiff for this.
			 */
			if (inFrontOfAgent(agentPosition, direction, cell.getPoint())) {
				for (Fire f : cell.items()) {
					toBeExtinguished.add(f);
				}
			}
			for (Fire f : toBeExtinguished) {
				f.extinguish();
			}
		}
	}
	
	public void checkWeather() {
		// TODO: Need to check rain and wind. First need to know how these are modeled.
		
	}
	
	public void setTargetFire(Fire targetFire) {
		this.targetFire = targetFire;
	}

	/**
	 * Check whether the parameter object position is either directly or
	 * diagonally in front of the parameter agent position, given the
	 * agent's direction
	 * @param agentPos
	 * @param agentDir
	 * @param objPos
	 * @return
	 */
	private boolean inFrontOfAgent(GridPoint agentPos, Directions agentDir, GridPoint objPos) {
		return agentPos.getX() + agentDir.xDiff == objPos.getX()
				|| agentPos.getY() + agentDir.yDiff == objPos.getY();
	}
	

}
