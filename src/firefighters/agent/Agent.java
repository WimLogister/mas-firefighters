package firefighters.agent;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import firefighters.utils.Directions;
import firefighters.world.Fire;

/**
 * 
 */
public abstract class Agent {
	
	Grid<Object> grid;
	double money;
	Vector2 velocity;
	Fire targetFire;
	int lifePoints; // Extra help value to prevent problems killing the agents in the Fire-class
	
  public Agent(Grid<Object> grid, double money, Vector2 velocity) {
    this.grid = grid;
    this.money = money;
    this.velocity = velocity;
    this.lifePoints = 1; 
  }

  @ScheduledMethod(start = 1, interval = 1, priority = -1)
	public void step() {
	  if(lifePoints == 0) kill();
	  else if (checkDeath()) kill();
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
	 * Move this agent in its current direction.
	 * Movement is a stochastic process: each agent's movement speed is modeled as 
	 * the probability of moving to the square it is currently facing.
	 */
	public void move() {
		if (RandomHelper.nextDouble() < velocity.len()) {
			GridPoint pt = grid.getLocation(this);
			Directions dir = Directions.fromVectorToDir(velocity);
			grid.moveTo(this, pt.getX()+dir.xDiff, pt.getY()+dir.yDiff);
		}
	}
	
	/**
	 * Let the agent turn in a given angle, counter-clockwise
	 */
	public void turn(float angle) {
		velocity.setAngle(velocity.angle()+angle).clamp(0, SimulationConstants.MAX_FIRE_AGENT_SPEED);
	}
	
	/**
	 * Fight this agent's target fire.
	 * Should first verify somehow that the agent is actually adjacent to a fire. 
	 */
	public void extinguish() {
		targetFire.extinguish();
	}
	
	public void checkWeather() {
		// TODO: Need to check rain and wind. Need to know how we want to use this in agent's AI/plans.
		
	}
	
	public void setTargetFire(Fire targetFire) {
		this.targetFire = targetFire;
	}
	
	/**
	 * If fire is in reach of the firefighter
	 * So if the fire is in the 3 directions in front of him
	 */
	public boolean isFireInReach(int x, int y){
		Directions dir = Directions.fromVectorToDir(velocity);	
		return isFireInReachDir(dir, x, y);
	}
	
	/**
	 * Is fire in reach given another direction of the firefighter
	 */	
	public boolean isFireInReachDir(Directions dir, int x, int y){
		boolean isReachable=false;
		
		GridPoint pt = grid.getLocation(this);
		int cX = pt.getX() + dir.xDiff;
		int cY = pt.getY() + dir.yDiff;
		
		if(x==cX && y==cY) isReachable = true;
		
		// 2 other "front"-locations
		float right = Math.abs(velocity.angle() - 45f ) % 360;
		float left = Math.abs(velocity.angle() + 45f) % 360;
		float[] toCheck = {left,right};
		for(float i: toCheck){
			dir = Directions.fromAngleToDir(i);
			int cX2 = pt.getX() + dir.xDiff;
			int cY2 = pt.getY() + dir.yDiff;
			if(x==cX2 && y==cY2) isReachable = true;
		}
		return isReachable;
	}
	
	public void setLifePoints(int i){
		this.lifePoints = i;
	}
}
