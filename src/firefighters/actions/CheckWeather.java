package firefighters.actions;

import lombok.Getter;
import firefighters.agent.Agent;
import firefighters.information.WeatherInformation;

public class CheckWeather implements PrimitiveAction{
	
	@Getter
	private static WeatherInformation weather;
	
	@Override
	public void execute(Agent agent) {
		weather = agent.checkWeather();
	}

	@Override
	/** Agent can always check the weather */
	public boolean checkPreconditions(Agent agent) {
		return true;
	}
}
