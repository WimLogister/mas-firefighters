package communication.information;

import lombok.Getter;
import repast.simphony.space.grid.GridPoint;
import agent.FirefighterAgent;

@Getter
public class AgentLocationInformation
    extends InformationPiece {

  /** x and y coordinate in the grid of the square that is on fire */
  private GridPoint position;
  
  /** The agent whose position is  described*/
  private FirefighterAgent agent;

  public AgentLocationInformation(FirefighterAgent agent, int x, int y) {
    super(InformationType.AgentPosition);
    this.agent = agent;
    this.position = new GridPoint(x, y);
  }
}
