package firefighters.agent;

import static constants.SimulationConstants.BOUNTY_PER_FIRE_EXTINGUISHED;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import lombok.Getter;
import constants.SimulationParameters;

/** Statistics about the agents operating in the world */
@Getter
public class AgentStatistics {

  private int startingAgentCount;

  /** Number of agents currently alive */
  private int liveAgentCount;

  /** Bounties collected from extinguishing fires, not from other agents */
  private int totalBountiesCollected;
  
  private int bountiesMean;

  public AgentStatistics() {
    startingAgentCount = SimulationParameters.agentCount;
  }
  
  @ScheduledMethod(start = 1, interval = 1)
  public void calculateMean(){
	  if(liveAgentCount == 0) bountiesMean = 0;
	  else {
		  bountiesMean = totalBountiesCollected / liveAgentCount;
	  }
  }

  public void addAgent() {
    liveAgentCount++;
  }

  public void removeAgent() {
    liveAgentCount--;
  }

  public void addBounty() {
    totalBountiesCollected += BOUNTY_PER_FIRE_EXTINGUISHED;
    //System.out.println(RunEnvironment.getInstance().getCurrentSchedule().getTickCount());
    //System.out.println(totalBountiesCollected);
  }
}
