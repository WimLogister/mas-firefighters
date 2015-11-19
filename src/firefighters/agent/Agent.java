package firefighters.agent;

import static firefighters.utils.GridFunctions.getCellNeighborhood;
import static firefighters.utils.GridFunctions.isInFrontOfAgent;

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
import firefighters.actions.Plan;
import firefighters.actions.Planner;
import firefighters.utility.UtilityFunction;
import firefighters.utils.Directions;
import firefighters.world.Fire;

/** The only distinction between agents is going to be their Behavior implementation, so this class is final */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Agent {
	
  /** Reference to the grid */
  @NonNull
	final Grid<Object> grid;

	final double movementSpeed;
  double money;
  /** The distance at which the agent can perceive the world around him, i.e. the status of the cells */
  final int perceptionRange;

  /** The direction the agent is facing */
  Directions direction;

  /** Using to devise the agent's plans */
  Planner planner;

  /** The agent's current plan */
  Plan currentPlan;

  public Agent(Grid<Object> grid,
               double movementSpeed,
               double money,
               int perceptionRange,
               UtilityFunction utilityFunction) {
    this.grid = grid;
    this.movementSpeed = movementSpeed;
    this.money = money;
    this.direction = Directions.getRandomDirection();
    this.perceptionRange = perceptionRange;

    planner = new Planner(utilityFunction);
  }

  @ScheduledMethod(start = 1, interval = 1)
	public void step() {
		if (checkDeath()) kill();
    // TODO Check if we should revise the plan
    if (currentPlan == null || currentPlan.isFinished()) {
      currentPlan = planner.devisePlan(this);
    }
    executeCurrentAction();
  }
  
  public void executeCurrentAction() {
    if (currentPlan != null && !currentPlan.isFinished()) {
      currentPlan.executeNextStep(this);
    }
  }
	
	/**
	 * Check for death condition: being surrounded by fire
	 */
	public boolean checkDeath() {
		// Set up necessary operators
		GridPoint cpt = grid.getLocation(this);
    List<GridCell<Fire>> gridCells = getCellNeighborhood(grid, cpt, Fire.class, 1, false);

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
      if (isInFrontOfAgent(agentPosition, direction, cell.getPoint())) {

				for (Fire f : cell.items()) {
					toBeExtinguished.add(f);
				}
			}
			for (Fire f : toBeExtinguished) {
				f.extinguish();
			}
      // Make sure we don't hose multiple fires in the same turn
      if (toBeExtinguished.size() > 0) {
        return;
      }
		}
	}
	
  public void hose(GridPoint firePosition) {
    GridPoint agentPosition = grid.getLocation(this);

    List<Fire> toBeExtinguished = new ArrayList<Fire>();

    List<GridCell<Fire>> fireList = getCellNeighborhood(grid, agentPosition, Fire.class, 0, true);
    for (GridCell<Fire> cell : fireList) {
      Iterable<Fire> firesInCellIterator = cell.items();
      int numFires = 0;
      for (Fire f : firesInCellIterator) {
        toBeExtinguished.add(f);
        numFires++;
      }
      assert numFires == 1 : "More than 1 fire cell founnd: " + numFires;
    }
  }

  /** Returns a list of the locations of fire cells the agent knows of */
  public List<GridCell<Fire>> getKnownFireLocations() {
    GridPoint agentPosition = grid.getLocation(this);
    return getCellNeighborhood(grid, agentPosition, Fire.class, perceptionRange, false);
  }

	public void checkWeather() {
		// TODO: Need to check rain and wind. First need to know how these are modeled.
		
	}
	

}
