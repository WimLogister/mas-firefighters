package firefighters.utils;

import repast.simphony.space.grid.GridPoint;

public final class Metrics {

  public static double hammingDistance(GridPoint pt1, GridPoint pt2) {
    return Math.max(Math.abs(pt1.getX() - pt2.getX()), Math.abs(pt1.getY() - pt2.getY()));
  }
	
	public static double manhattanDistance(GridPoint pt1, GridPoint pt2) {
    return Math.abs(pt1.getX() - pt2.getX()) + Math.abs(pt1.getY() - pt2.getY());
	}

	  public static double euclideanDistance(GridPoint pt1, GridPoint pt2) {
	    double dxSquared = Math.pow(pt1.getX() - pt2.getX(), 2);
	    double dySquared = Math.pow(pt1.getY() - pt2.getY(), 2);
	    return Math.sqrt(dxSquared + dySquared);
	  }

}
