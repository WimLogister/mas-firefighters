package constants;

import java.util.Random;

public class SimulationConstants {

  public static final float MAX_FIRE_SPEED = 0.5f;
  
  public static final float MAX_FIRE_AGENT_SPEED = 2 * MAX_FIRE_SPEED;
 
  /** Chance with which fire can appear out of nowhere */
  public static final double FIRE_PROB = 0.01;

  /** Chance with which rain can appear */
  public static final double RAIN_PROB = 0.2;
  
  /** Change with which wind is changing direction */
  public static final double WIND_CHANGE_PROB = 0.1;
  
  /** The distance around him that an agent can perceive the environment */
  public static final int AGENT_PERCEPTION_DISTANCE = 5;

  /** The reward for each fire cell extinguished */
  public static final int BOUNTY_PER_FIRE_EXTINGUISHED = 100;

  /** Random instance */
  public static final Random RANDOM = new Random();

  public static final float MAX_WIND_SPEED = 2;

  /** The agent's won't find paths to a fire further than this distance */
  public static final int MAX_SEARCH_DISTANCE = 15;

  /** Messages sent locally will be received by agents closer than this distance away */
  public static final int LOCAL_MESSAGE_RANGE = 10;

}
