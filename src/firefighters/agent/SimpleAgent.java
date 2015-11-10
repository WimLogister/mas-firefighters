package firefighters.agent;

import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import firefighters.utils.Directions;
import firefighters.world.Fire;

/** Agent that moves towards the closest fire */
public class SimpleAgent
    extends Agent {

  /** The distance at which the agent can perceive the world around him, i.e. the status of the cells */
  private final int perceptionDistance = 6;

  public SimpleAgent(Grid<Object> grid, double movementSpeed, double money) {
    super(grid, movementSpeed, money);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void step() {
    if (checkDeath()) {
      kill();
      return;
    }
    GridPoint agentPosition = grid.getLocation(this);
    Fire closestFire = findClosestFire();
    GridPoint fireLocation = grid.getLocation(closestFire);
    if (fireLocation != null) {
      setTargetFire(closestFire);
      int dx = (int) Math.signum(fireLocation.getX() - agentPosition.getX());
      int dy = (int) Math.signum(fireLocation.getY() - agentPosition.getY());
      if (matches(direction, dx, dy)) {
        double distance = euclideanDistance(agentPosition, fireLocation);
        if (distance <= 1) {
          extinguish();
        } else {
          move();
        }
      } else {
        for(Directions dir : Directions.values()) {
          if (matches(dir, dx, dy)) {
            turn(dir);
          }
        }
      }
    }
  }

  public Fire findClosestFire() {
    GridPoint agentPosition = grid.getLocation(this);
    GridCellNgh<Fire> ngh = new GridCellNgh<>(grid, agentPosition, Fire.class, perceptionDistance, perceptionDistance);
    List<GridCell<Fire>> gridCells = ngh.getNeighborhood(false);

    // Need at least four fires in neighborhood in order to be surrounded
    if (gridCells.size() == 0)
      return null;

    double minDistance = Double.POSITIVE_INFINITY;
    Fire closest = null;

    for (GridCell<Fire> cell : gridCells) {
      for (Fire fire : cell.items()) {
        GridPoint firePoint = grid.getLocation(fire);
        double distance = euclideanDistance(firePoint, agentPosition);
        if (distance < minDistance) {
          minDistance = distance;
          closest = fire;
        }
      }
    }
    return closest;
  }

  // TODO Move to some util class
  public static double euclideanDistance(GridPoint pointA, GridPoint pointB) {
    double dxSquared = Math.pow(pointA.getX() - pointB.getX(), 2);
    double dySquared = Math.pow(pointA.getY() - pointB.getY(), 2);
    return Math.sqrt(dxSquared + dySquared);
  }

  public static boolean matches(Directions direction, int dx, int dy) {
    return direction.xDiff == dx || direction.yDiff == dy;
  }

}
