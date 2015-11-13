package communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import firefighters.agent.Agent;

/** Any message sent by an agent. */
@Getter
@AllArgsConstructor
public class Message {

  /** The agent sending the message */
  private Agent sender;

  /** The scope of the message, global or local */
  private MessageScope scope;
  
  /** The information content of the message */
  private MessageContent content;
}
