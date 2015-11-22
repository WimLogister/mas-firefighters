package communication.information;

import java.util.List;

public class AgentInformationStore {

  private List<InformationPiece> informationStore;

  /** The maximum number of information pieces to be stored */
  private final int memorySize = 50;

  private int index = 0;

  public void archive(InformationPiece informationPiece) {
    if (informationStore.size() < memorySize)
      informationStore.add(informationPiece);
    else
      informationStore.set(index, informationPiece);
    index = (index + 1) % memorySize;
  }
}
