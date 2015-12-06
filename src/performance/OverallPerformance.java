package performance;

import repast.simphony.engine.schedule.ScheduledMethod;
import constants.SimulationConstants;
import constants.SimulationParameters;
import firefighters.world.Wind;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * How well the agents perform in terms of conditions of the world, weather, fire-category,
 * human and forest losses and achievements such as the number of fires extinguished
 * TODO: Seperate evaluation function which covers:
 * Cumulative bounty collected
 * Mean of individual bounty
 * Resources spent on communication
 * Resources spent on task allocation
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
		this.humanLosses = 0;
		this.forestLosses = 0;
		this.firesExtinguished = 0;
		this.performance = 0;
	}
	
	public void init(){
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
		return SimulationConstants.MAX_WIND_SPEED / Wind.getWindVelocity().len();
	}
	
	public double calculate(){
		double forestLostPer = ((double) forestLosses / (double)(SimulationParameters.gridSize * SimulationParameters.gridSize)) * 100;
		double humanLostPer = ((double) humanLosses / (double) SimulationParameters.agentCount) * 100;		
		double firesExtinguishedPer = ((double) firesExtinguished / (double)(fireCount + SimulationParameters.fireCount)) * 100;
		double rainPer = ((double) rainCount / (double)(SimulationParameters.gridSize * SimulationParameters.gridSize)) * 100;
		double windPer = getWindFraction() * 100;
		
		// Value between 0 and 10.000
		System.out.println("ratioPoints " + ratioPoints + " ratioFireAgent " + ratioFireAgent + " forestLostPer " + forestLostPer + " humanlostper " + humanLostPer + " firesexper " + firesExtinguishedPer + " rain per " + rainPer + " windPer " + windPer);
		performance = (this.ratioPoints + this.ratioFireAgent) * (windPer + (100-rainPer) + 3 * firesExtinguishedPer + 5 * (100-humanLostPer) - 2 * forestLostPer);
		if(performance<0) performance = 0;
		if(performance>10000) performance = 10000;
		
		System.out.println(performance);
		this.performance = performance;
		return performance;
	}
}