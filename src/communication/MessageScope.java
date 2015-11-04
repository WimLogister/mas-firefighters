package communication;

/**
 * The scope of a message. Messages with global scope are broadcast to all agents, those with local scope only in the
 * sender's neighborhood
 */
public enum MessageScope {
  GLOBAL,
  LOCAL;
}
