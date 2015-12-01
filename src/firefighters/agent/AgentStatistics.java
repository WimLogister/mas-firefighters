package firefighters.agent;

import static constants.SimulationConstants.BOUNTY_PER_FIRE_EXTINGUISHED;
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

  public AgentStatistics() {
    startingAgentCount = SimulationParameters.agentCount;
  }

  public void addAgent() {
    liveAgentCount++;
  }

  public void removeAgent() {
    liveAgentCount--;
  }

  public void addBounty() {
    totalBountiesCollected += BOUNTY_PER_FIRE_EXTINGUISHED;
  }
}
