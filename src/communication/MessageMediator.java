package communication;

import static constants.SimulationConstants.GLOBAL_MESSAGE_COST;
import static constants.SimulationConstants.LOCAL_MESSAGE_COST;
import static constants.SimulationConstants.LOCAL_MESSAGE_RANGE;
import static firefighters.utils.GridFunctions.getCellNeighborhood;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.Grid;
import firefighters.agent.Agent;

// Note: All messages should probably be sent at the beginning of each iteration to ensure each agent can act on the messages received
/**
 * Class responsible for message exchange between agents. All agent messages are sent through this
 * class
 */
public class MessageMediator {

  private static final MessageMediator INSTANCE = new MessageMediator();

  private MessageMediator() {
    activeAgents = new ArrayList<>();
  }

  /** List of all the agents in the world */
  private List<Agent> activeAgents;

  /** Needs to be called for each agent to allow global messages */
  public static void registerAgent(Agent agent) {
    INSTANCE.activeAgents.add(agent);
  }

  /** Called when an agent is removed from the world */
  public static void deregisterAgent(Agent agent) {
    INSTANCE.activeAgents.remove(agent);
  }

  public static void sendMessage(Message message) {
    if (message.getScope() == MessageScope.LOCAL)
      INSTANCE.broadcastLocally(message);
    else if (message.getScope() == MessageScope.GLOBAL)
      INSTANCE.broadcastToAll(message);
  }

  /** Sends a message to all other agents */
  private void broadcastToAll(Message message) {
    message.getSender().subtractMoney(GLOBAL_MESSAGE_COST);
    sendMessageTo(message, activeAgents);
  }

  private void sendMessageTo(Message message, List<Agent> agents) {
    for (Agent agent : agents) {
      if (agent != message.getSender()) {
        agent.messageReceived(message);
      }
    }
  }

  /**
   * Sends a message to all other agents in the agent's neighborhood
   */
  private void broadcastLocally(Message message) {
    Agent sender = message.getSender();
    sender.subtractMoney(LOCAL_MESSAGE_COST);

    Grid<Object> grid = sender.getGrid();
    List<Agent> receivers = new ArrayList<Agent>();
    List<GridCell<Agent>> agentCells = getCellNeighborhood(grid,
                                                           grid.getLocation(sender),
                                                           Agent.class,
                                                           LOCAL_MESSAGE_RANGE,
                                                           true);
    for (GridCell<Agent> cell : agentCells) {
      for (Agent agent : cell.items()) {
        receivers.add(agent);
      }
    }
    sendMessageTo(message, receivers);
  }
}