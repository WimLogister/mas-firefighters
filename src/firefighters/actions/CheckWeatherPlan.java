package firefighters.actions;

import java.util.List;

import lombok.Getter;
import repast.simphony.space.grid.GridPoint;

/** CheckWeatherPlan is consisting of only one action: CheckWeather */
public class CheckWeatherPlan extends Plan {

	public CheckWeatherPlan(List<AbstractAction> steps) {
		super(steps);
	}
}
