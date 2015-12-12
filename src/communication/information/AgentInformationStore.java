package communication.information;

import java.util.ArrayList;
import java.util.List;

public class AgentInformationStore {

  private List<InformationPiece> informationStore;

  /** The maximum number of information pieces to be stored */
  private final int memorySize = 50;

  private int index = 0;

  public AgentInformationStore() {
    super();
    informationStore = new ArrayList<>();
  }

  public void archive(InformationPiece informationPiece) {
    if (informationStore.size() < memorySize)
      informationStore.add(informationPiece);
    else
      informationStore.set(index, informationPiece);
    index = (index + 1) % memorySize;
  }

  /** Erases all information */
  public void clear() {
    informationStore.clear();
  }
  
  public void clearType(InformationType type){
    List<InformationPiece> toRemove = new ArrayList<>();
	for(InformationPiece p : informationStore){
	  if(p.getInformationType().equals(type)) {
	    toRemove.add(p);
	  }
	}
	for(InformationPiece p : toRemove){
	  informationStore.remove(p);
	}
  }
  
  public InformationPiece getLatestInformationOfType(InformationType type){
	  if(informationStore.isEmpty()) return null;
	  for(int n = informationStore.size()-1; n>=0; n--){
		  InformationPiece p = informationStore.get(n);
		  if(p.getInformationType().equals(type)) return p;
	  }
	  return null;
  }

  /** Erases all information of the specified type */
  public <T extends InformationPiece> void clear(Class<T> informationClass) {
    List<InformationPiece> retainedInformation = new ArrayList<>();
    for (InformationPiece infoPiece : informationStore) {
      if (infoPiece.getClass() != informationClass) {
        retainedInformation.add(infoPiece);
      }
    }
    informationStore = retainedInformation;
  }

  /**
   * Get information of a specific type. Example usage: <br>
   * {@code List<HelpRequestInformation> helpRequests = informationStore.getInformationOfType(HelpRequestInformation.class)}
   * 
   * @param informationClass
   * @return
   */
  public <T extends InformationPiece> List<T> getInformationOfType(Class<T> informationClass) {
    List<T> matchingInformation = new ArrayList<>();
    for (InformationPiece piece : informationStore) {
      if (piece.getClass() == informationClass) {
        T cast = (T) piece;
        matchingInformation.add(cast);
      }
    }
    return matchingInformation;
  }
}
