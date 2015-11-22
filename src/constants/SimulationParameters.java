package constants;

import repast.simphony.parameter.Parameters;

public class SimulationParameters {

  /** The dimensions of the grid, width and height are equal */
  public static int gridSize = 50;

  /** How many steps it takes before the tree-grid has burned down completely */
  public static int lifePointsTree;

  public static int lifePointsFire;

  /** How many fires we initialize with */
  public static int fireCount;

  // /** How much rain we initialize with */
  // public static int rainCount;

  /** How many agents we start with */
  public static int agentCount;

  /** Sets the simulation's parameters */
  public static void setParameters(Parameters params) {
    gridSize = (Integer) params.getValue("grid_size");
    lifePointsTree = (Integer) params.getValue("life_points_tree"); // How many steps it takes before the tree-grid has
                                                                    // burned down completely
    lifePointsFire = (Integer) params.getValue("life_points_fire");
    fireCount = (Integer) params.getValue("fire_count"); // How many fires we initialize with
    // rainCount = (Integer) params.getValue("rain_count"); // How much rain we initialize with
    agentCount = (Integer) params.getValue("agent_count"); // How many agents we start with
  }
}
