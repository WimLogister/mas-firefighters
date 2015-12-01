package constants;

import repast.simphony.parameter.Parameters;

public class SimulationParameters {

  /** The dimensions of the grid, width and height are equal */
  public static int gridSize = 50;

  /** How many steps it takes before the tree-grid has burned down completely */
  public static int lifePointsTree;

  /** How many steps it takes before the fire can be extinguished */
  public static int lifePointsFire;

  /** How many fires we initialize with */
  public static int fireCount;

  /** How many agents we start with */
  public static int agentCount;
  
  /** How far the agents can look */
  public static int perceptionRange;
  
  /** Initial direction of the wind */
  public static float windDirection;
  
  /** Whether agents will share information about the fire */
  public static boolean cooperativeAgents = true;

  /** 
   * We can set different types of weather (sunny, rainy, cloudy and windy) which result in different values for
   * velocity of the wind, quantity of rain, the chance with which fire can appear out of nowhere, etc.
   */
  public static String weather;

  /** Sets the simulation's parameters */
  public static void setParameters(Parameters params) {
    gridSize = (Integer) params.getValue("grid_size");
    lifePointsTree = (Integer) params.getValue("life_points_tree");
    lifePointsFire = (Integer) params.getValue("life_points_fire");
    fireCount = (Integer) params.getValue("fire_count");
    agentCount = (Integer) params.getValue("agent_count"); 
    perceptionRange = (Integer) params.getValue("perception_range"); 
    windDirection = ((Float) params.getValue("wind_direction"));
    weather = (String) params.getValue("weather");
  }
}
