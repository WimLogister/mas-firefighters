package communication.information;

import lombok.Getter;
import repast.simphony.space.grid.GridPoint;
import firefighters.agent.Agent;

@Getter
public class AgentLocationInformation
    extends InformationPiece {

  /** The location of the agent making the request */
  private GridPoint position;
  
  /** The agent whose position is  described*/
  private Agent agent;

  public AgentLocationInformation(Agent agent, int x, int y) {
    super(InformationType.AgentPosition);
    this.agent = agent;
    this.position = new GridPoint(x, y);
  }
}
