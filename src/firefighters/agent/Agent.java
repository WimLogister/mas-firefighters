package firefighters.agent;

import static firefighters.utils.GridFunctions.getCellNeighborhood;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
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

  @Setter
  int lifePoints = 1;

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
    if (lifePoints == 0 || checkDeath()) {
      kill();
      return;
    }
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

  /** Extinguish fire in a specific grid position */
  public void hose(GridPoint firePosition) {

    List<Fire> toBeExtinguished = new ArrayList<Fire>();
    List<GridCell<Fire>> fireList = getCellNeighborhood(grid, firePosition, Fire.class, 0, true);
    for (GridCell<Fire> cell : fireList) {
      Iterable<Fire> firesInCellIterator = cell.items();
      int numFires = 0;
      for (Fire f : firesInCellIterator) {
        toBeExtinguished.add(f);
        numFires++;
      }
      assert numFires == 1 : "More than 1 fire cell founnd: " + numFires;
    }
    for (Fire f : toBeExtinguished) {
      f.extinguish();
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
