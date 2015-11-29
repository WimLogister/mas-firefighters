package firefighters.utility;

import firefighters.actions.AbstractAction;
import firefighters.actions.CheckWeather;
import firefighters.actions.Extinguish;

public class CheckWeatherUtilityFunction extends DiscountedUtilityFunction{

	@Override
	public double calculateUtility(AbstractAction action) {
		if(action instanceof CheckWeather){
			return 1000;
		}
		else return 0;
	}

}
