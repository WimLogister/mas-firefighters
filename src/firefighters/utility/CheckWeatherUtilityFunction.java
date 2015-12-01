package firefighters.utility;

import repast.simphony.engine.schedule.ScheduledMethod;
import firefighters.actions.AbstractAction;
import firefighters.actions.CheckWeather;
import firefighters.actions.Extinguish;

public class CheckWeatherUtilityFunction extends DiscountedUtilityFunction{

	int tick;
	
	public CheckWeatherUtilityFunction(){
		this.tick = 0;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		tick++;
	}

	@Override
	public double calculateUtility(AbstractAction action) {
		double result = 0;
		if(action instanceof CheckWeather){
			//System.out.println("Weather last checked utility function: " + ((CheckWeather) action).getLastChecked());
			//int timeNotChecked = tick - ((CheckWeather) action).getLastChecked();
			int timeNotChecked = tick;
			if(timeNotChecked >= 30) result = 300;
			else if(timeNotChecked < 30 && timeNotChecked >=20) result = 200;
			else if(timeNotChecked < 20 && timeNotChecked >=15) result = 100;
			else if (timeNotChecked < 15 && timeNotChecked >=10) result = 75;
			else if (timeNotChecked < 10 && timeNotChecked >=5) result = 50;
			else if (timeNotChecked < 5 && timeNotChecked >=4) result = 0;
			else if (timeNotChecked < 4 && timeNotChecked >=3) result = -50;
			else if (timeNotChecked < 3 && timeNotChecked >=2) result = -100;
			else result = -200;		
			System.out.println(result);
		}
		return result;
	}
}
