package constants;

import java.util.Random;

public class SimulationConstants {

  public static final float MAX_FIRE_SPEED = 0.5f;
  
  public static final float MAX_FIRE_AGENT_SPEED = 2 * MAX_FIRE_SPEED;
  
  public static final float MAX_WIND_SPEED = 2;
  
  /** The life points of each agent, fire deals 1 damage per step */
  public static final int AGENT_LIFE_POINTS = 3;

  /** The reward for each fire cell extinguished */
  public static final int BOUNTY_PER_FIRE_EXTINGUISHED = 100;

  /** Random instance */
  public static final Random RANDOM = new Random();
  
  /** The agent's won't find paths to a fire further than this distance */
  public static final int MAX_SEARCH_DISTANCE = 15;

  /** Messages sent locally will be received by agents closer than this distance away */
  public static final int LOCAL_MESSAGE_RANGE = 10;

  /** The cost of sending a message locally */
  public static final int LOCAL_MESSAGE_COST = 1;

  /** The cost of sending a message locally */
  public static final int GLOBAL_MESSAGE_COST = 5 * LOCAL_MESSAGE_COST;

}
