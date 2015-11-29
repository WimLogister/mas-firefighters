package firefighters.actions;

<<<<<<< HEAD
import lombok.Getter;
import firefighters.agent.Agent;
import firefighters.information.WeatherInformation;

public class CheckWeather implements PrimitiveAction{
	
	@Getter
	private static WeatherInformation weather;
	
	//public static WeatherInformation getWeather(){
	//	return weather;
	//}

	@Override
	public void execute(Agent agent) {
		weather = agent.checkWeather();
	}

	@Override
	/**
	 * Agent can always check the weather
	 */
	public boolean checkPreconditions(Agent agent) {
		return true;
	}
=======
import firefighters.agent.Agent;

public class CheckWeather implements PrimitiveAction {

	@Override
	public void execute(Agent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkPreconditions(Agent agent) {
		// TODO Auto-generated method stub
		return false;
	}

>>>>>>> master
}
