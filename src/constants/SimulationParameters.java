package constants;

import firefighters.utils.Directions;
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
  
  /** Chance with which fire can appear with every tick */
  public static double fireProb;

  /** How many agents we start with */
  public static int agentCount;
  
  /** How far the agents can look */
  public static int perceptionRange;
  
  /** Initial direction of the wind */
  public static Directions windDirection;
  
  /** Fraction of the maximum wind speed, value between 0 and 1*/
  public static float windSpeed;
  
  /** Value between 0 and 1, how much wind velocity vector can change every time step*/
  public static float windInstability;
  
  /** Probability with which rain can appear, value between 0 and 1*/
  public static double rainProb;
  
  /** Average grid size of the rain */
  public static int averageRainSize;
  
  /** Whether agents will share information about the fire */
  public static boolean cooperativeAgents = true;

  /** Sets the simulation's parameters */
  public static void setParameters(Parameters params) {
    gridSize = (Integer) params.getValue("grid_size");
    lifePointsTree = (Integer) params.getValue("life_points_tree");
    lifePointsFire = (Integer) params.getValue("life_points_fire");
    fireCount = (Integer) params.getValue("fire_count");
    fireProb = (Double) params.getValue("fire_prob");
    checkBound("fire_prob",fireProb);
    agentCount = (Integer) params.getValue("agent_count"); 
    perceptionRange = (Integer) params.getValue("perception_range"); 
    String windD = ((String) params.getValue("wind_direction"));
    windDirection = Directions.fromStringToDir(windD);
    windSpeed = ((Float) params.getValue("wind_speed"));
    checkBound("wind_speed",windSpeed);
    windInstability = ((Float) params.getValue("wind_instability"));
    checkBound("wind_instability",windInstability);
    rainProb = ((Double) params.getValue("rain_prob"));
    checkBound("rain_prob",rainProb);
    averageRainSize = (Integer) params.getValue("average_rain_size");
    if(averageRainSize<0|averageRainSize>gridSize)
    	throw new IllegalArgumentException("Value of average rain size out of range!");
  }
  
  private static void checkBound(String param, float x){
	  if(x<0||x>1) throw new IllegalArgumentException("Value of parameter " + param + " is out of range!");
  }
  
  private static void checkBound(String param, double x){
	  if(x<0||x>1) throw new IllegalArgumentException("Value of parameter " + param + " is out of range!");
  }
  
}
