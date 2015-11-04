package communication.information;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Abstraction of a single piece of information */
@AllArgsConstructor
public abstract class InformationPiece {

  /** The type of information, see {@link InformationType} */
  @Getter
  private InformationType informationType;
}
