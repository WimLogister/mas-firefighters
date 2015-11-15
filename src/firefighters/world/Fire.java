package firefighters.world;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;
import cern.jet.random.Uniform;
import constants.SimulationConstants;
import firefighters.agent.Agent;
import firefighters.utils.Direction;

/*
 * TODO:
 * Appear(): FIX: now fires might randomly appear at already burned grids
 */
public class Fire {
	
	Random rand = new Random();
	private static final Uniform urng = RandomHelper.getUniform();
	
	private Grid<Object> grid;
	private Vector2 velocity; 
	// Fire has certain number of lifePoints which decreases it is being hosed by an agent
	private int lifePoints;
	private int maxLifePoints;
	private double fireProb;
	
	public Fire(Grid<Object> grid, Vector2 velocity, int lifePoints, int maxLifePoints, double fireProb) {
		this.grid = grid;
		if(velocity.len() > SimulationConstants.MAX_FIRE_SPEED){
			throw new IllegalArgumentException("Speed value of fire is out of range!");
		} else this.velocity = velocity;
		this.lifePoints = lifePoints;
		this.maxLifePoints = lifePoints; 
		this.fireProb = fireProb;
	}
	
	/**
	 * With each step (and in this order): 
	 * Fires can be extinguished, this method is called by the agent who is performing this action.
	 * Fire burns down the trees (decreasing lifepoints of the trees)
	 * Fire may change its speed and direction according to the rain, wind and hosing of a firefighter
	 * Fire may spread to new area (according to direction) with certain chance
	 * Wildfires can appear suddenly in any part of the forest with a small chance
	 */
	@ScheduledMethod(start = 1, interval = 1, priority =0)
	public void step(){
		burn();
		updateVelocity();
		spread();
		appear();
		removeFire();
	}
	
	/**
	 *  If there is a firefighter hosing the fire, the fire decreases in its lifepoints and speed
	 */
	public void extinguish(){
		this.lifePoints--;
		if(lifePoints<=0) setSpeed(0);
		else {
			// Substracting a percentage of the maximum fire-speed of from the fire
			float newSpeed = this.getSpeed() - SimulationConstants.MAX_FIRE_SPEED * 0.5f;
			this.setSpeed(newSpeed);
		}
	}		
	
	/**
	 * Fire grows in strength by burning trees
	 * If trees are almost burned (life-points <=1) fire lessens in strength
	 * Fire is burning the tree in the current grid (= decreasing its life-points)
	 */
	public void burn(){
		GridPoint pt = grid.getLocation(this);
		Tree treeToBurn = null;
		// Check if there is a Tree object in this grid cell it will be decremented in its life-points.
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
			if (obj instanceof Tree) treeToBurn = (Tree) obj;
		}
		if(!(treeToBurn == null)) {
			treeToBurn.decrementLifePoints();
			if(treeToBurn.getLifePoints() > 1) incrementLifePoints();
			// Fire cannot decrease in strength in such a way that it dies out
			else if (lifePoints > 1) decrementLifePoints();			
		}
	}	
	
	/**
	 * Removing the fire from the grid if it does not have sufficient life-points
	 */
	public void removeFire(){
		if(lifePoints <= 0) ContextUtils.getContext(this).remove(this);
		else {
			GridPoint pt = grid.getLocation(this);
			if(!containsTree(pt)) ContextUtils.getContext(this).remove(this);
		}
	}
	
	/**
	 * Method to check if a given gridpoint contains a tree
	 */
	public boolean containsTree(GridPoint pt){
		boolean containsTree = false;
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
			if (obj instanceof Tree) containsTree = true;
		}
		return containsTree;
	}
	
	/**
	 * Method to check if it is raining in the direction in which the fire is heading to
	 * That means we look at if there is rain in the 3 adjacent cells in the direction of the fire
	 * EG: Fire has direction of 90 degrees (= North), we look at the cells North, North-West and North-East
	 * If fire has direction of 135 degrees (= North-West), we look at the cells North-West, North and West
	 * We return the number of these cells containing rain.
	 */
	public int checkRainInHeading(){
		int noRain = 0;
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Rain> ngh = new GridCellNgh<>(grid, pt, Rain.class, 1, 1);
		List<GridCell<Rain>> gridCells = ngh.getNeighborhood(false);
		
		// If there is no rain at all, return false
		if(gridCells.size()==0) return 0;
		
		Direction direction = new Direction(); // Direction of the fire in grid cell coordinates		
		float angle = velocity.angle(); // Angle of the fire
		
		// If there is rain in the grid the fire is heading to, return true
		boolean isRaining = false;
		for (GridCell<Rain> cell : gridCells) {
			for (Rain rain : cell.items()) {
				GridPoint rainpt = grid.getLocation(rain);  
				// Get xDiff and yDiff from 3 grids 'in front of' the fire
				float right = Math.abs(angle - 45f ) % 360;
				float left = Math.abs(angle + 45f) % 360;
				float[] toCheck = {left,angle,right};
				for(float i : toCheck){
					direction.fromAngleToDir(i);
					if (rainpt.getX() == pt.getX()+direction.xDiff && rainpt.getY() == pt.getY()+direction.yDiff) noRain++;
				}
			}
		}
		return noRain;
	}
	
	/**
	 * Check if it's raining in the current location of the fire
	 */	
	public boolean checkRainInLocation(){
		boolean isRaining = false;
		GridPoint pt = grid.getLocation(this);
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
			if (obj instanceof Rain) isRaining = true;
		}	
		return isRaining;
	}
	
	/**
	 * SPEED
	 * If there is no rain in the direction of the movement of the wildfire, it speeds up. 
	 * If there is rain in its heading, then it slows down (if there are 3 grid cells containing rain, 
	 * rain slows down more than with 2 cells, same for 2 and 1 cell)
	 * If there is rain at the location of the fire itself, speed slows down even more
	 * 
	 * DIRECTION
	 * Fire is influenced by the direction of the wind by adding the wind's velocity vector to that of the fire
	 * 
	 * LIFEPOINTS
	 * Rain in the current grid reduces the lifepoints of the fire by 1
	 */
	public void updateVelocity(){
		// Adjust velocity vector of fire according to that of the wind
		velocity.add(getCurrentWindVelocity()).clamp(0, SimulationConstants.MAX_FIRE_SPEED);	
		
		int noRainHeading = checkRainInHeading();
		// If it is raining the current grid
		if(checkRainInLocation()){
			decrementLifePoints();
			float newSpeed = this.getSpeed() - SimulationConstants.MAX_FIRE_SPEED * 0.4f;
			this.setSpeed(newSpeed);
		}
		else if(noRainHeading==3){
			float newSpeed = this.getSpeed() - SimulationConstants.MAX_FIRE_SPEED * 0.3f;
			this.setSpeed(newSpeed);
		}
		else if(noRainHeading==2){
			float newSpeed = this.getSpeed() - SimulationConstants.MAX_FIRE_SPEED * 0.2f;
			this.setSpeed(newSpeed);
		}
		else if(noRainHeading==1){
			float newSpeed = this.getSpeed() - SimulationConstants.MAX_FIRE_SPEED * 0.1f;
			this.setSpeed(newSpeed);
		}
		else{
			float newSpeed = this.getSpeed() + SimulationConstants.MAX_FIRE_SPEED * 0.1f;
			this.setSpeed(newSpeed);
		}
		
	}
	
	/**
	 * Fire spreads to the next grid in its direction with the highest chance (= speed), but can also spread to other directions with a lower chance
	 */
	public void spread(){
		double spreadToDirInFront = getSpeed(); // value between 0 and 1, for example direction is North
		double spreadTo2ClosestDirs = spreadToDirInFront * 0.1; // directions are North-East and North-West
		double spreadToOther = spreadTo2ClosestDirs * 0.01; // Chance with which to spread to another direction than the fire's own direction and the 2 closest directions
		
		// Spreading to the direction the fire is directly heading 
		Direction dir = new Direction();
		dir.discretizeVector(velocity);
		spreadStepFire(dir,spreadToDirInFront);
				
		// 2 other "front"-locations
		float right = Math.abs(velocity.angle() - 45f ) % 360;
		float left = Math.abs(velocity.angle() + 45f) % 360;
		float[] toCheck = {left,right};
		for(float i: toCheck){
			dir.fromAngleToDir(i);
			spreadStepFire(dir,spreadTo2ClosestDirs);			
		}
		// All other locations
		float j = left;
		for(int i=0; i<5;i++){
			j = (j+45f)%360;
			dir.fromAngleToDir(j);
			spreadStepFire(dir,spreadToOther);
		}
	}
	
	/**
	 * Given a direction, moves this fire to the direction with a certain chance
	 * @param dir: direction to move to 
	 * @param chance: chance to move with
	 */	
	public void spreadStepFire(Direction dir, double chance){
		GridPoint pt = grid.getLocation(this);
		int cX = pt.getX() + dir.xDiff;
		int cY = pt.getY() + dir.yDiff;
		int[] cLoc = {cX, cY}; // Get location of grid to which the fire possibly spreads
		if(canSpread(cLoc)){
			double test = urng.nextDouble();
			System.out.println(test + " :: " + chance);
			if (test < chance) { // Spreads with certain "speed" (modeled in stochastic way)
				Fire fire = new Fire(grid, velocity.clamp(0, SimulationConstants.MAX_FIRE_SPEED), lifePoints, maxLifePoints,fireProb); // Fire spreads with same direction, speed and number of lifepoints
				ContextUtils.getContext(this).add(fire);
				grid.moveTo(fire, cX, cY);
				// If there is a firefighter at the new location, the firefighter is being killed
				for (Object object : grid.getObjectsAt(cLoc)){
					if(object instanceof Agent) ((Agent) object).setLifePoints(0);
				}
			}
		}
	}
	
	/**
	 *  Can only spread to new area if this area is a forest which is not already burned and does not contain a fire already
	 */
	public boolean canSpread(int[] location){
		boolean spreadPossible = true;
		boolean containsTree = false;
		for (Object object : grid.getObjectsAt(location)){
			if(object instanceof Tree) containsTree = true;
			else if(object instanceof Fire) spreadPossible = false;
		}
		if(!containsTree) spreadPossible = false;
		return spreadPossible;
	}
	
	/**
	 * Fires can appear suddenly in any part of the forest with a certain chance
	 * They appear with the direction of the wind
	 */
	public void appear(){
		if (urng.nextDouble() < fireProb) {
			RandomGridAdder<Object> ra = new RandomGridAdder<Object>();
			Vector2 fire_vel = new Vector2();
			fire_vel.x = rand.nextFloat() * (SimulationConstants.MAX_FIRE_SPEED - 0) + 0;
			fire_vel.setAngle(rand.nextFloat() * (360 - 0) + 0);
			Fire fire = new Fire(grid,fire_vel,1,1,fireProb);
			ContextUtils.getContext(this).add(fire);
			ra.add(grid, fire);
		}
	}
	
	public float getSpeed(){
		return this.velocity.len();
	}
	
	/**
	 * Setting the speed is setting the length of the velocity vector to this value
	 */
	public void setSpeed(float speed){
		velocity.setLength(speed);
		velocity.clamp(0, SimulationConstants.MAX_FIRE_SPEED);
	}
	
	public float getDirection(){
		return this.velocity.angle();
	}
	
	public void setDirection(float angle){
		this.velocity.setAngle(angle);
	}
	
	public int getLifePoints(){
		return lifePoints;
	}
	
	public void setVelocity(Vector2 velocity){
		this.velocity = velocity;
	}
	
	public Vector2 getVelocity(){
		return velocity;
	}
	
	/**
	 * If lifepoints are 0, set the speed of the fire to 0 so that it can't spread anymore
	 */
	public void decrementLifePoints(){
		this.lifePoints--;
		if(lifePoints<=0) setSpeed(0);
	}
	
	/**
	 * Not possible to set lifepoints of fire higher than the maximum number of lifepoints
	 */	
	public void setLifePoints(int lifePoints){
		if(lifePoints > maxLifePoints) this.lifePoints = maxLifePoints;
		else this.lifePoints = lifePoints;
	}
	
	public void incrementLifePoints(){
		if(lifePoints < maxLifePoints) lifePoints ++;
	}
	
	public Vector2 getCurrentWindVelocity(){
		IndexedIterable<Wind> winds = ContextUtils.getContext(this).getObjects(Wind.class);
		Wind currentWind = winds.iterator().next();
		return currentWind.getVelocity();
	}
}
