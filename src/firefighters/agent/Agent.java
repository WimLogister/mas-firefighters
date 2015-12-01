package firefighters.agent;

import static constants.SimulationConstants.BOUNTY_PER_FIRE_EXTINGUISHED;
import static firefighters.utils.GridFunctions.getCellNeighborhood;
import static firefighters.utils.GridFunctions.isOnFire;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

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
import repast.simphony.util.collections.IndexedIterable;
import firefighters.actions.AbstractAction;
import firefighters.actions.CheckWeather;
import firefighters.actions.ExtinguishFirePlan;
import firefighters.actions.MoveAndTurn;
import firefighters.actions.Plan;
import firefighters.actions.Planner;
import firefighters.actions.PrimitiveAction;
import firefighters.information.WeatherInformation;
import firefighters.utility.UtilityFunction;
import firefighters.utils.Directions;
import firefighters.world.Fire;
import firefighters.world.Rain;
import firefighters.world.TreeBuilder;
import firefighters.world.Wind;

/** The only distinction between agents is going to be their Behavior implementation, so this class is final */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Agent {
	
  /** Reference to the grid */
  @NonNull
	final Grid<Object> grid;

	final double movementSpeed;

	@Getter
  double money;
  /** The distance at which the agent can perceive the world around him, i.e. the status of the cells */
  final int perceptionRange;

  /** The direction the agent is facing */
  Directions direction;

  /** Using to devise the agent's plans */
  Planner planner;

  /** The agent's current plan */
  Plan currentPlan;
  
  UtilityFunction utilityFunction;
  
  AbstractAction previousAction;
  
  @Getter @Setter
  List<GridPoint> knownFireLocations;

  @Setter
  int lifePoints = 1;
  
  @Getter
  int tickWeatherLastChecked;
  
  int tick;

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
    this.utilityFunction = utilityFunction;
    this.previousAction = null;
    this.knownFireLocations = new ArrayList<GridPoint>();
    this.tickWeatherLastChecked = 0;
    this.tick = 0;

    planner = new Planner(utilityFunction);
  }

  @ScheduledMethod(start = 1, interval = 1)
	public void step() {
	  tick++;
    if (lifePoints == 0 || checkDeath()) {
      kill();
      return;
    }
    if(!(previousAction instanceof CheckWeather)){
    	//System.out.println("Previous action was not checking weather");
    	knownFireLocations = lookForFires();
    }
    // TODO Check if we should revise the plan
    if (currentPlan == null || currentPlan.isFinished() || !isValid(currentPlan)) {
      currentPlan = planner.devisePlan(this);
    }
    executeCurrentAction();
  }
  
  /** Checks if the current plan is still valid */
  private boolean isValid(Plan currentPlan) {
    if (currentPlan instanceof ExtinguishFirePlan) {
      GridPoint fireLocation = ((ExtinguishFirePlan) currentPlan).getFireLocation();
      if (!isOnFire(grid, fireLocation))
        return false;
    }
    List<AbstractAction> steps = currentPlan.getSteps();
    for (int i = 0; i < steps.size(); i++) {
      AbstractAction action = steps.get(i);
      if (action instanceof MoveAndTurn) {
        MoveAndTurn moveAction = (MoveAndTurn) action;
        GridPoint position = moveAction.getNewPos();
        if (isOnFire(grid, position))
          return false;
      }
    }
    return true;
  }

  public void executeCurrentAction() {
    if (currentPlan != null && !currentPlan.isFinished()) {
      previousAction = currentPlan.getSteps().get(0);
      //System.out.println(previousAction);
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
		TreeBuilder.performance.increaseHumanLosses();
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
      for(Fire f : toBeExtinguished){
    	  f.extinguish();
      }
    }
    for (Fire f : toBeExtinguished) {
      f.extinguish();
      if (f.getLifePoints() == 0) {
        // Fire is extinguished, receive bounty
        // TODO If 2 agents hose a fire in the same step they probably should receive half each
        money += BOUNTY_PER_FIRE_EXTINGUISHED;
      }
    }
    for (Fire f : toBeExtinguished) {
      f.extinguish();
      if (f.getLifePoints() == 0) {
        // Fire is extinguished, receive bounty
        // TODO If 2 agents hose a fire in the same step they probably should receive half each
        money += BOUNTY_PER_FIRE_EXTINGUISHED;
      }
    }
  }

  /** Returns a list of the locations of fire cells the agent knows of */
  public List<GridPoint> lookForFires() {
    GridPoint agentPosition = grid.getLocation(this);
    List<GridCell<Fire>> fireCells = getCellNeighborhood(grid, agentPosition, Fire.class, perceptionRange, false);
    List<GridPoint> firePoints = new ArrayList<GridPoint>();
    for (GridCell<Fire> fireCell : fireCells) {
        GridPoint firePoint = fireCell.getPoint();
        firePoints.add(firePoint);
    }
    return firePoints;
  }

  public WeatherInformation checkWeather() {
	tickWeatherLastChecked = tick;
  	System.out.println("weather last checked agent: " + tickWeatherLastChecked);
    Vector2 wind = getCurrentWindVelocity();
	// Check if there is rain in the agent's it surroundings
    GridPoint agentPosition = grid.getLocation(this);
    List<GridCell<Rain>> rain = getCellNeighborhood(grid, agentPosition, Rain.class, perceptionRange, true);
    WeatherInformation currentWeather = new WeatherInformation(wind,rain);
    return currentWeather;
  }

  public Vector2 getCurrentWindVelocity(){
	IndexedIterable<Wind> winds = ContextUtils.getContext(this).getObjects(Wind.class);
	Wind currentWind = winds.iterator().next();
	return currentWind.getVelocity();
  }

}
