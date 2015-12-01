package communication.information;

import lombok.Getter;
import repast.simphony.space.grid.GridPoint;
import firefighters.agent.Agent;

@Getter
public class HelpRequestInformation
    extends InformationPiece {

  /** The agent who is requesting help */
  private Agent agent;

  /** The location of the agent making the request */
  private GridPoint agentLocation;

  /** Offered in exchange for help. Still need to decide when and if it will be awarded */
  private double bounty;

  public HelpRequestInformation(Agent agent, GridPoint agentLocation, double bounty) {
    super(InformationType.HelpRequest);
    this.agent = agent;
    this.agentLocation = agentLocation;
    this.bounty = bounty;
  }

}
