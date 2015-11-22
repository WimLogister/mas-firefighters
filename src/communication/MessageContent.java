package communication;

import lombok.AllArgsConstructor;
import lombok.Getter;

import communication.information.InformationPiece;

@AllArgsConstructor
public class MessageContent {

  @Getter
  private InformationPiece information;
}
