package firefighters.utility;

import firefighters.actions.AbstractAction;
import firefighters.agent.Agent;
import firefighters.utils.TickCounter;

public class WeatherUtilityFunction extends DiscountedUtilityFunction{

	@Override
	public double calculateUtility(AbstractAction action, Agent agent) {
		int timeNotChecked = TickCounter.getTick() - agent.getTickWeatherLastChecked();
		double bonusNotChecked = 0;
		if(timeNotChecked > 0 && timeNotChecked <= 5) bonusNotChecked = 25;
		else if(timeNotChecked > 5 && timeNotChecked <= 10) bonusNotChecked = 50;
		else if(timeNotChecked > 10 && timeNotChecked <= 25 ) bonusNotChecked = 75;
		else if(timeNotChecked > 25) bonusNotChecked = 100;
		return bonusNotChecked;
	}

}
