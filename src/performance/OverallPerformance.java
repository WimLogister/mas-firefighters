package performance;

import lombok.Getter;
import lombok.Setter;
import repast.simphony.engine.schedule.ScheduledMethod;
import constants.SimulationParameters;

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
	private int difficulty;
	@Getter @Setter
	private double performance;
	
	public OverallPerformance(){
		this.humanLosses = 0;
		this.forestLosses = 0;
		this.firesExtinguished = 0;
		// Influence of weather on difficulty for fighting the fire
		this.difficulty = 1;
		this.performance = 0;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority =0)
	public void step(){
    this.setPerformance(calculate());
    // System.out.println(RunEnvironment.getInstance().getCurrentSchedule().getTickCount() + " " + performance);
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
	
	public double calculate(){
		double forestLostPer = ((double)forestLosses / (double)(SimulationParameters.gridSize * SimulationParameters.gridSize)) * 100;
		double humanLostPer = ((double)humanLosses / (double) SimulationParameters.agentCount) * 100;
		
		// Category of fire, determined by combination of forest losses and human losses
		int category;
		if(forestLostPer + humanLostPer < 25) category = 1;
		else if(forestLostPer + humanLostPer < 50) category = 2;
		else if(forestLostPer + humanLostPer < 75) category = 3;
		else if(forestLostPer + humanLostPer < 100) category = 4;
		else if(forestLostPer + humanLostPer < 150) category = 5;
		else category = 6;
			
		int noStartFires = SimulationParameters.fireCount;
		int lifePointsFire = SimulationParameters.lifePointsFire;
		int lifePointsTree = SimulationParameters.lifePointsTree;
		
		double firesExtinguishedPer = ((double)firesExtinguished / (double)(fireCount + noStartFires)) * 100;
		
		return firesExtinguishedPer + category * 10 + difficulty * 20 + noStartFires * 10 + lifePointsFire * 10 - forestLostPer - humanLostPer - (lifePointsTree -1) * 10;
		
	}
	

}
