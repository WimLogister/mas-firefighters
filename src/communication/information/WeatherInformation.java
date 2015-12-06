package communication.information;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import firefighters.world.Rain;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.space.grid.GridPoint;
import lombok.Getter;

@Getter
public class WeatherInformation extends InformationPiece {

	@Getter
	private Vector2 wind;
	
	/** Rain objects within the perception range of the agent */
	@Getter
	private List<GridCell<Rain>> rain;

	public WeatherInformation(Vector2 wind, List<GridCell<Rain>> rain) {
		super(InformationType.WeatherInformation);
		this.wind = wind;
		this.rain = rain;
	}
}
