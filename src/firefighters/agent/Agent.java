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
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

import com.badlogic.gdx.math.Vector2;
import communication.Message;
import communication.MessageContent;
import communication.MessageMediator;
import communication.MessageScope;
import communication.information.AgentInformationStore;
import communication.information.AgentLocationInformation;
import communication.information.FireLocationInformation;
import communication.information.HelpRequestInformation;
import communication.information.InformationPiece;
import communication.information.WeatherInformation;

import constants.SimulationConstants;
import constants.SimulationParameters;
import firefighters.actions.AbstractAction;
import firefighters.actions.CheckWeather;
import firefighters.actions.ExtinguishFirePlan;
import firefighters.actions.MoveAndTurn;
import firefighters.actions.Plan;
import firefighters.actions.Planner;
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

  double communicationProb;
  
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
  
  int tickWeatherLastChecked;
  int currentTick;
  
  @Getter
  private GridPoint agentPosition;

  public Agent(Grid<Object> grid,
               double movementSpeed,
               double money,
               int perceptionRange,
               UtilityFunction utilityFunction,
               double communicationProb) {
    this.grid = grid;
    this.movementSpeed = movementSpeed;
    this.money = money;
    this.direction = Directions.getRandomDirection();
    this.perceptionRange = perceptionRange;
    this.utilityFunction = utilityFunction;
    this.tickWeatherLastChecked = -SimulationConstants.RANDOM.nextInt(26);
    this.communicationProb = communicationProb;
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
	  currentTick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	  agentPosition = grid.getLocation(this); 
    if (lifePoints == 0 || checkDeath()) {
      kill();
      return;
    }
    processInformationAndCommunicate();
    // Emergency plan for if the fire is likely to spread to the agent's current cell
    if (this.danger(this.getAgentPosition())>1.5){
    	currentPlan = planner.deviseEmergencyPlan(this);
    }
    else if (currentPlan == null || currentPlan.isFinished() || !isValid(currentPlan)) {
      if(SimulationConstants.RANDOM.nextDouble()<probabilityOfWeatherChecking()){
    	  List<AbstractAction> steps = new ArrayList<AbstractAction>();
          steps.add(new CheckWeather());
          currentPlan = new Plan(steps);
      } else{
    	  currentPlan = planner.devisePlan(this);
      }
    }
    executeCurrentAction();
  }
  
  private double probabilityOfWeatherChecking(){
	  int timeNotChecked = currentTick - tickWeatherLastChecked;
	  if(SimulationParameters.useWeatherInformation){
		  if(timeNotChecked<=1) return 0;
		  if(timeNotChecked>1 && timeNotChecked<=5) return 0.05;
		  if(timeNotChecked>5 && timeNotChecked<=10) return 0.1;
		  if(timeNotChecked>10 && timeNotChecked<=15) return 0.2;
		  if(timeNotChecked>15 && timeNotChecked<=20) return 0.3;
		  else return 0.5;
	  } else {
		  return 0;
	  }
  }
  
  private void processInformationAndCommunicate() {
    List<GridPoint> newFires = updateFireInformation();
    if(willShare()) {
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

    informationStore.clear(FireLocationInformation.class);
    for (GridCell<Fire> fireCell : fireCells) {
      GridPoint firePoint = fireCell.getPoint();
      FireLocationInformation fireInformation = new FireLocationInformation(firePoint.getX(), firePoint.getY());
      informationStore.archive(fireInformation);
    }
    return newFires;
  }

  private List<GridCell<Fire>> findFiresInNeighborhood() {
    // Update fire information
    List<GridCell<Fire>> fireCells = getCellNeighborhood(grid, agentPosition, Fire.class, perceptionRange, true);
    return fireCells;
  }

  private List<GridCell<Agent>> findAgentsInNeighborhood() {
    // Update fire information
    List<GridCell<Agent>> agentCells = getCellNeighborhood(grid, agentPosition, Agent.class, perceptionRange, true);
    return agentCells;
  }

  /** Returns whether the agent is in danger and should request for assistance */
  private boolean isInDanger() {
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
    List<GridCell<Fire>> gridCells = getCellNeighborhood(grid, agentPosition, Fire.class, 1, false);

		// Need at least four fires in neighborhood in order to be surrounded
		if (gridCells.size() < 4) return false;

		// If at least four fires in neighborhood, check that they are immediately
		// surrounding the agent, i.e. two fires on same y-axis, two fires on same x-axis
		int enclosedVertical = 0;
		int enclosedHorizontal = 0;
		for (GridCell<Fire> cell : gridCells) {
			for (Fire fire : cell.items()) {
				GridPoint firept = grid.getLocation(fire);
        if (firept.getX() == agentPosition.getX())
          enclosedHorizontal++;
        if (firept.getY() == agentPosition.getY())
          enclosedVertical++;
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
			/*
			 * Move the agent according to its current direction. How the direction
			 * influences its movement in the grid is modelled by the Directions Enum,
			 * which is used here.
			 */
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
      assert numFires == 1 : "More than 1 fire cell found: " + numFires;
    }
    for (Fire f : toBeExtinguished) {
      f.extinguish();
      if (f.getLifePoints() == 0) {
        // Fire is extinguished, receive bounty
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
    AgentLocationInformation location = new AgentLocationInformation(this, agentPosition.getX(), agentPosition.getY());
    sendLocalMessage(location);
  }

  /** Sends a help request message */
  private void sendHelpRequest(int bountyOffered) {
    GridPoint position = grid.getLocation(this);
    HelpRequestInformation helpRequest = new HelpRequestInformation(this, position, bountyOffered);
    sendLocalMessage(helpRequest);
  }
  
  /** Communicates the information about the wind globally */
  private void communicateWindInfo() {
    if (this.hasWeatherInfo()) {
      WeatherInformation weather = (WeatherInformation) informationStore.getInformationOfType(WeatherInformation.class);
      Vector2 windInfo = weather.getWind();
      WeatherInformation toCommunicate = new WeatherInformation(windInfo, new ArrayList<GridCell<Rain>>(), currentTick);
      sendGlobalMessage(toCommunicate);
    }
  }

  private void sendLocalMessage(InformationPiece information) {
	if(money >= SimulationConstants.LOCAL_MESSAGE_COST){
		Message message = new Message(this, MessageScope.LOCAL, new MessageContent(information));
		MessageMediator.sendMessage(message);
	}
  }
  
  private void sendGlobalMessage(InformationPiece information) {
	if(money >= SimulationConstants.GLOBAL_MESSAGE_COST){
		Message message = new Message(this, MessageScope.GLOBAL, new MessageContent(information));
		MessageMediator.sendMessage(message);
	}
  }

  /** Returns a list of the locations of fire cells the agent knows of */
  public List<FireLocationInformation> getKnownFireLocations() {
    return informationStore.getInformationOfType(FireLocationInformation.class);
  }
  
  public WeatherInformation getWeatherInformation(){
	  return (WeatherInformation) informationStore.getInformationOfType(WeatherInformation.class);
  }

  /**
   * Check weather and store this information in agent's information store Agent communicates this information directly
   * with certain probability
   */
  public void checkWeather() {
    // Clears any old weather information
    informationStore.clear(WeatherInformation.class);
    Vector2 wind = Wind.getWindVelocity();
    // Check if there is rain in the agent's it surroundings
    GridPoint agentPosition = grid.getLocation(this);
    List<GridCell<Rain>> rain = getCellNeighborhood(grid, agentPosition, Rain.class, perceptionRange, true);
    WeatherInformation currentWeather = new WeatherInformation(wind, rain, currentTick);
    informationStore.archive(currentWeather);
    this.tickWeatherLastChecked = currentTick;
    if (willShare()) {
		// Agent only communicates the wind information
		WeatherInformation toCommunicate = new WeatherInformation(wind, new ArrayList<GridCell<Rain>>(),currentTick);
		sendGlobalMessage(toCommunicate);
    }
  }

  public boolean willShare(){
	  double test = SimulationConstants.RANDOM.nextDouble();
    if (test < communicationProb)
      return true;
	  else return false;		  
  }
  
  // Double between 1 and 4 for the amount of danger of being in the position newPoint considering
  // chance that fire will spread to this point the next step.
  // Also used for cost calculation in Astar algorithm.
  public double danger(GridPoint toCheck){
	double danger = 1;
	if(this.hasWeatherInfo()){
		int toCheckX = toCheck.getX();
		int toCheckY = toCheck.getY();
		List<WeatherInformation> weatherInfos = this.getInformationStore().getInformationOfType(WeatherInformation.class);
		// Take the last piece of weather information
		WeatherInformation weatherInfo = weatherInfos.get(weatherInfos.size()-1);
		int timePassed = currentTick - weatherInfo.getTimeStamp();
		if(timePassed<10){
			List<GridCell<Rain>> rainInfo = weatherInfo.getRain();
	
			// Check if there is rain in the gridcell
			boolean rainInNewPoint = false;
			for(GridCell<Rain> rain : rainInfo){
				if(rain.getPoint().getX() == toCheckX && rain.getPoint().getY() == toCheckY) rainInNewPoint = true;
			}

			// Loop through all the neighboring firecells
			for (GridCell<Fire> fireCell : getCellNeighborhood(grid, toCheck, Fire.class, 1, false)) {
				GridPoint pt = fireCell.getPoint();
				int fireX = pt.getX();
				int fireY = pt.getY();
				Vector2 fireVelocity = null;
				boolean rainInFireLoc = false;
				for (Object obj : grid.getObjectsAt(fireX, fireY)){
					if (obj instanceof Fire) fireVelocity = ((Fire) obj).getVelocity();
					//else if (obj instanceof Rain) rainInFireLoc = true;
				}	
			
				// If there is rain in the fire's location
				for(GridCell<Rain> rain : rainInfo){
					if(rain.getPoint().getX() == fireX && rain.getPoint().getY() == fireY) rainInFireLoc = true;
				}
		
				Vector2 windVelocity = weatherInfo.getWind();
				// Influence of wind on fire directions  
				if(!fireVelocity.equals(null)){
					fireVelocity.add(windVelocity).clamp(0, SimulationConstants.MAX_FIRE_SPEED);
					if(rainInFireLoc) {
						float newSpeed = fireVelocity.len() - SimulationConstants.MAX_FIRE_SPEED * 0.4f;
						fireVelocity.setLength(newSpeed);
						fireVelocity.clamp(0, SimulationConstants.MAX_FIRE_SPEED);
					}
				}
				else {
					throw new IllegalArgumentException("This is not supposed to happen! In GridSuccessorFunction.java");
				}
				// Determine if the updated fire direction is towards newPoint
				Directions dir = Directions.fromAngleToDir(fireVelocity.angle());
				boolean headingTowardsNewPoint = false;
				if(fireX + dir.xDiff == toCheck.getX() && fireY + dir.yDiff == toCheck.getY()) headingTowardsNewPoint = true;
				
				if(headingTowardsNewPoint){
					// Has to do with the way the rain is influencing the speed of the fire in Fire.java
					if(!rainInFireLoc && rainInNewPoint){
						float newSpeed = fireVelocity.len() - SimulationConstants.MAX_FIRE_SPEED * 0.2f;
              fireVelocity.setLength(newSpeed);
						fireVelocity.clamp(0, SimulationConstants.MAX_FIRE_SPEED);
					}
					// Value between 0 and 1
					double spreadChange = fireVelocity.len()*(1/SimulationConstants.MAX_FIRE_SPEED);
					danger = danger + spreadChange;
				}
			}
		}
	}
	if(danger<1||danger>4) 
		throw new IllegalArgumentException("Calculated cost is out of range!");
	return danger;
  }

  public void messageReceived(Message message) {
    // If message is of type WeatherInformation, then check if there is any older weather information in the information
    // store and delete that first, to keep the most up to date version
    if (message.getInformationContent() instanceof WeatherInformation) {
      informationStore.clear(WeatherInformation.class);
    }
    informationStore.archive(message.getInformationContent());
  }

  public void decrementLifePoints() {
    lifePoints--;
  }
  
  public boolean hasWeatherInfo(){
	  if(this.getInformationStore().getInformationOfType(WeatherInformation.class).isEmpty()) return false;
	  else return true;
  }
}
