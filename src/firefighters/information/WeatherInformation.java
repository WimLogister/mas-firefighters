package firefighters.information;

import java.util.List;

import repast.simphony.query.space.grid.GridCell;

import com.badlogic.gdx.math.Vector2;

import firefighters.world.Rain;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WeatherInformation {
	
	@Getter
	private Vector2 wind;
	@Getter
	private List<GridCell<Rain>> rain;

}
