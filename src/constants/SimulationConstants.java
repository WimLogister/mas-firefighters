package constants;


public class SimulationConstants {

  public static final int MAX_FIRE_SPEED = 1;
  
  public static final int MAX_FIRE_AGENT_SPEED = 2 * MAX_FIRE_SPEED;
  
  /** Chance with which fire can appear out of nowhere */
  public static final double FIRE_PROB = 0.01;

  /** Chance with which rain can appear */
  public static final double RAIN_PROB = 0.2;
  
  /** Change with which wind is changing direction */
  public static final double WIND_CHANGE_PROB = 0.1;
  
  /** The distance around him that an agent can perceive the environment */
  public static final int AGENT_PERCEPTION_DISTANCE = 5;

}
