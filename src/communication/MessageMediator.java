package communication;

import java.util.List;

import firefighters.agent.Agent;

// Note: All messages should probably be sent at the beginning of each iteration to ensure each agent can act on the messages received
/**
 * Class responsible for message exchange between agents. All agent messages are sent through this
 * class
 */
public class MessageMediator {

  /** List of all the agents in the world */
  private List<Agent> activeAgents;

  /** Sends a message to all other agents */
  public void broadcastToAll(Message message) {
    for (Agent agent : activeAgents) {
      if (agent != message.getSender()) {
        agent.messageReceived(message);
      }
    }
  }

  /**
   * Sends a message to all other agents in the agent's neighborhood
   */
  public static void broadcastLocally(Message message) {

  }

  // TODO Support sending a message to a specific set of agents
}
