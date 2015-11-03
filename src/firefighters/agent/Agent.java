package firefighters.agent;

import java.util.Iterator;
import java.util.List;

import firefighters.utils.Directions;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.MultiOccupancyCellAccessor;
import repast.simphony.util.ContextUtils;

public abstract class Agent {
	
	Grid<Object> grid;
	double movementSpeed;
	double money;
	Directions direction;
	Fire targetFire;
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		if (checkDeath()) die();
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
		if (gridCells.size() < 4) return;

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
	 * Remove agent from the simulation
	 */
	private void die() {
		ContextUtils.getContext(this).remove(this);
	}
	
	/**
	 * Move this agent in its current direction.
	 * Movement is a stochastic process: each agent's movement speed is modeled as 
	 * the probability of moving to the square it is currently facing.
	 */
	public void move() {
		if (RandomHelper.nextDouble() < movementSpeed) {
			GridPoint pt = grid.getLocation(this);
			/*
			 * Move the agent according to its current direction. How the direction
			 * influences its movement in the grid is modeled by the Directions Enum,
			 * which is used here.
			 */
			grid.moveTo(this, pt.getX()+direction.xDiff, pt.getY()+direction.yDiff);
		}
	}
	
	public void turn(Directions direction) {
		this.direction = direction;
	}
	
	/**
	 * Fight this agent's target fire.
	 * Should first verify somehow that the agent is actually adjacent to a fire. 
	 */
	public void extinguish() {
		targetFire.extinguish();
	}
	
	public abstract void checkWeather();
	
	public void setTargetFire(Fire targetFire) {
		this.targetFire = targetFire;
	}
	
	class Fire {
		public Fire() {
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * Placeholder method to model decreasing this fire's life points.
		 */
		public void extinguish() {
			
		}
	}

}
