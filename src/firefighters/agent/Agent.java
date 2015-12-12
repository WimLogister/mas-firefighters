package firefighters.agent;

import static constants.SimulationConstants.AGENT_LIFE_POINTS;
import static constants.SimulationConstants.BOUNTY_PER_FIRE_EXTINGUISHED;
import static firefighters.utils.GridFunctions.getCellNeighborhood;
import static firefighters.utils.GridFunctions.isOnFire;

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

import communication.Message;
import communication.MessageContent;
import communication.MessageMediator;
import communication.MessageScope;
import communication.information.AgentInformationStore;
import communication.information.AgentLocationInformation;
import communication.information.FireLocationInformation;
import communication.information.HelpRequestInformation;
import communication.information.InformationPiece;

import constants.SimulationParameters;
import firefighters.actions.AbstractAction;
import firefighters.actions.ExtinguishFirePlan;
import firefighters.actions.MoveAndTurn;
import firefighters.actions.Plan;
import firefighters.actions.Planner;
import firefighters.utility.UtilityFunction;
import firefighters.utils.Directions;
import firefighters.world.Fire;
import firefighters.world.TreeBuilder;

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

  /** The life points of the agent, should be more than 1 to give them a chance of escaping the fire */
  @Setter(AccessLevel.PRIVATE)
  int lifePoints = AGENT_LIFE_POINTS;

  AgentInformationStore informationStore;

  /** Ratio of fires to agents in the region to request help */
  double firesToAgentsDangerThreshold = 5;
      
  /** The number of agents alive in the world */
  static AgentStatistics agentStatistics = new AgentStatistics();

  // TODO Combine with the utility function
  /** If true new fires will be broadcast to other agents */
  private boolean shareFireInformation = SimulationParameters.cooperativeAgents;

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

    this.informationStore = new AgentInformationStore();
    this.planner = new Planner(utilityFunction);

    MessageMediator.registerAgent(this);
    agentStatistics.addAgent();
  }

  public void subtractMoney(int amount) {
    money -= amount;
  }

  @ScheduledMethod(start = 1, interval = 1)
	public void step() {
    if (lifePoints == 0 || checkDeath()) {
      kill();
      return;
    }
    processInformationAndCommunicate();
    // TODO Check if we should revise the plan
    if (currentPlan == null || currentPlan.isFinished() || !isValid(currentPlan)) {
      currentPlan = planner.devisePlan(this);
    }
    executeCurrentAction();


  }
  
  private void processInformationAndCommunicate() {
    List<GridPoint> newFires = updateFireInformation();
    if(shareFireInformation) {
      for(GridPoint newFirePoint : newFires) {
        FireLocationInformation fireLocation = new FireLocationInformation(newFirePoint);
        sendLocalMessage(fireLocation);
      }
    }
    
    if (isInDanger()) {
      sendHelpRequest(BOUNTY_PER_FIRE_EXTINGUISHED / 2);
    }
  }

  private List<GridPoint> updateFireInformation() {
    List<FireLocationInformation> previouslyKnownFires = informationStore.getInformationOfType(FireLocationInformation.class);
    List<GridPoint> previouslyKnownFirePoints = new ArrayList<>();
    List<GridPoint> newFires = new ArrayList<>();
    for (FireLocationInformation fireInfo : previouslyKnownFires) {
      previouslyKnownFirePoints.add(fireInfo.getPosition());
    }

    List<GridCell<Fire>> fireCells = findFiresInNeighborhood();
    for (GridCell<Fire> fireCell : fireCells) {
      GridPoint firePoint = fireCell.getPoint();
      if (!previouslyKnownFirePoints.contains(firePoint)) {
        newFires.add(firePoint);
      }
    }

    // TODO Temporary before storing the time steps information was received
    informationStore.clear(FireLocationInformation.class);
    for (GridCell<Fire> fireCell : fireCells) {
      GridPoint firePoint = fireCell.getPoint();
      FireLocationInformation fireInformation = new FireLocationInformation(firePoint.getX(), firePoint.getY());
      informationStore.archive(fireInformation);
    }
    return newFires;
  }

  private List<GridCell<Fire>> findFiresInNeighborhood() {
    GridPoint position = grid.getLocation(this);
    // Update fire information
    List<GridCell<Fire>> fireCells = getCellNeighborhood(grid, position, Fire.class, perceptionRange, true);
    return fireCells;
  }

  private List<GridCell<Agent>> findAgentsInNeighborhood() {
    GridPoint position = grid.getLocation(this);
    // Update fire information
    List<GridCell<Agent>> agentCells = getCellNeighborhood(grid, position, Agent.class, perceptionRange, true);
    return agentCells;
  }

  /** Returns whether the agent is in danger and should request for assistance */
  private boolean isInDanger() {
    // TODO Determine some threshold
    int fireCount = findFiresInNeighborhood().size();
    int agentCount = findAgentsInNeighborhood().size();
    double ratio = 1.0 * fireCount / agentCount;
    return ratio > firesToAgentsDangerThreshold;
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
    MessageMediator.deregisterAgent(this);
    agentStatistics.removeAgent();
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
      if (f.getLifePoints() == 0) {
        // Fire is extinguished, receive bounty
        // TODO If 2 agents hose a fire in the same step they probably should receive half each
        receiveBounty();
      }
    }
  }

  private double receiveBounty() {
    agentStatistics.addBounty();
    return money += BOUNTY_PER_FIRE_EXTINGUISHED;
  }

  /** Communicates the agent's position locally */
  private void communicateLocation() {
    GridPoint position = grid.getLocation(this);
    AgentLocationInformation location = new AgentLocationInformation(this, position.getX(), position.getY());
    sendLocalMessage(location);
  }

  /** Sends a help request message */
  private void sendHelpRequest(int bountyOffered) {
    // System.out.println("Requesting help ");
    GridPoint position = grid.getLocation(this);
    HelpRequestInformation helpRequest = new HelpRequestInformation(this, position, bountyOffered);
    sendLocalMessage(helpRequest);
  }

  private void sendLocalMessage(InformationPiece information) {
    Message message = new Message(this, MessageScope.LOCAL, new MessageContent(information));
    MessageMediator.sendMessage(message);
  }

  /** Returns a list of the locations of fire cells the agent knows of */
  public List<FireLocationInformation> getKnownFireLocations() {
    return informationStore.getInformationOfType(FireLocationInformation.class);
  }

	public void checkWeather() {
		// TODO: Need to check rain and wind. First need to know how these are modeled.
	}

  public void messageReceived(Message message) {
    informationStore.archive(message.getInformationContent());
  }

  public void decrementLifePoints() {
    lifePoints--;
  }
	

}
