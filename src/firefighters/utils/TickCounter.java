package firefighters.utils;

import repast.simphony.engine.schedule.ScheduledMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class TickCounter {
	
	@Getter
	private static int tick;
	
	public TickCounter(){
		this.tick = 0;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void increaseTick(){
		tick++;
	}
}
