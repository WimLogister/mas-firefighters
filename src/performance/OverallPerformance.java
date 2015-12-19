package performance;

import lombok.Getter;
import lombok.Setter;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;
import constants.SimulationParameters;
import firefighters.world.Fire;
import firefighters.world.Wind;

/**
 * How well the agents perform in terms of conditions of the world, weather, fire-category,
 * human and forest losses and achievements such as the number of fires extinguished
 */
public class OverallPerformance {
	
	@Getter
	private int humanLosses;
	@Getter
	private int forestLosses;
	@Getter
	private int firesExtinguished;
	@Getter
	private int fireCount;
	@Getter
	private int rainCount;
	@Getter @Setter
	private double performance;
	private double ratioPoints;
	private double ratioFireAgent;
	
	public OverallPerformance(){
		initCounts();
	}

  private void initCounts() {
    this.humanLosses = 0;
		this.forestLosses = 0;
		this.firesExtinguished = 0;
		this.performance = 0;
    this.fireCount = 0;
  }
	
	public void init(){
    initCounts();
		// Difficulty is determined by the ratio of lifePointsTree : lifePointsFire
		// If the fire is stronger than the trees, the fire is harder to extinguish
		this.ratioPoints = (double) SimulationParameters.lifePointsFire / (double) SimulationParameters.lifePointsTree; 
		// Clamp values of ratio
		if(ratioPoints<0.5) ratioPoints = 0.5;
		if(ratioPoints>5) ratioPoints = 5;
		
		// Ratio initial number of fires : agents. Normalized by ratio 1 : 25
		this.ratioFireAgent = ((double) SimulationParameters.fireCount / (double) SimulationParameters.agentCount) / 0.04;
		if(ratioFireAgent<0.5) ratioFireAgent = 0.5;
		if(ratioFireAgent>5) ratioPoints = 5;

	}
	
	@ScheduledMethod(start = 1, interval = 1, priority =0)
	public void step(){
    this.setPerformance(calculate());
    int currFires = 0;
    for (Object o : ContextUtils.getContext(this).getAgentLayer(Fire.class)) {
      currFires++;
    }
    if (currFires == 0) {
      RunEnvironment.getInstance().endRun();
    }
	}
	
	public void increaseHumanLosses(){
		humanLosses++;
	}
	
	public void increaseForestLosses(){
		forestLosses++;
	}
	
	public void increaseFiresExtinguished(){
		firesExtinguished++;
	}
	
	public void increaseFireCount(){
		fireCount++;
	}
	
	public void increaseRainCount(){
		rainCount++;
	}
	
	public void decreaseRainCount(){
		rainCount--;
	}
	
	/** What fraction of the maximum wind speed is the current wind speed */
	public float getWindFraction(){
    return Wind.getWindVelocity().len() / SimulationParameters.maxWindSpeed;
	}
	
	public double calculate(){
		double forestLostPer = ((double) forestLosses / (double)(SimulationParameters.gridSize * SimulationParameters.gridSize)) * 100;
		double humanLostPer = ((double) humanLosses / (double) SimulationParameters.agentCount) * 100;		
		double firesExtinguishedPer = ((double) firesExtinguished / (double)(fireCount + SimulationParameters.fireCount)) * 100;
		double rainPer = ((double) rainCount / (double)(SimulationParameters.gridSize * SimulationParameters.gridSize)) * 100;
		double windPer = getWindFraction() * 100;
		
		// Value between 0 and 10.000
		performance = (this.ratioPoints + this.ratioFireAgent) * (windPer + (100-rainPer) + 3 * firesExtinguishedPer + 5 * (100-humanLostPer) - 2 * forestLostPer);
		if(performance<0) performance = 0;
		if(performance>10000) performance = 10000;
		
		return performance;
	}
}
