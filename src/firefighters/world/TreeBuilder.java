package firefighters.world;


import static constants.SimulationConstants.MAX_FIRE_AGENT_SPEED;
import static constants.SimulationParameters.gridSize;

import java.util.Random;

import performance.OverallPerformance;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import constants.SimulationParameters;
import firefighters.agent.Agent;
import firefighters.agent.AgentStatistics;
import firefighters.utility.ComponentsUtilityFunction;
import firefighters.utility.UtilityFunction;


public class TreeBuilder implements ContextBuilder<Object> {
	
	public static OverallPerformance performance = new OverallPerformance();
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("sample-simulation");

    RunEnvironment.getInstance().endAt(300);

		Random rand = new Random();
		Parameters params = RunEnvironment.getInstance().getParameters();
		SimulationParameters.setParameters(params);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid",
                                               context,
                                               new GridBuilderParameters<Object>(new StrictBorders(),
                                                                                 new SimpleGridAdder<Object>(),
                                                                                 true,
                                                                                 gridSize,
                                                                                 gridSize)); // Square grid, variable
                                                                                              // size
		GridDimensions dims = grid.getDimensions();
		RandomGridAdder<Object> ra = new RandomGridAdder<Object>(); // To random add objects in the space
		
		// Fill the grid completely with trees
		for (int d0=0; d0<dims.getDimension(0); d0++){
			for (int d1=0; d1<dims.getDimension(1); d1++){
				int[] nextLoc = {d0,d1};
				Tree tree = new Tree(grid,SimulationParameters.lifePointsTree);
				context.add(tree);
				grid.moveTo(tree, nextLoc);
			}
		}
				
		// Add wind to the forest
		Wind wind = new Wind(grid, SimulationParameters.windSpeed, SimulationParameters.windDirection, SimulationParameters.windInstability); 
		context.add(wind);
		
		// Adding performance to the context
		context.add(performance);
		
		// Add raincontext to the forest 
		RainContext rc = new RainContext(grid); 
		context.add(rc);
		
		AgentStatistics agentStatistics = new AgentStatistics();
		context.add(agentStatistics);
		
		// Randomly place agents of set 1 in grid
		int agentSet1 = (int) (SimulationParameters.proportionSet1 * SimulationParameters.agentCount);
		for (int i = 0; i < agentSet1; i++) {
			UtilityFunction utilityFunction = new ComponentsUtilityFunction(SimulationParameters.riskTakingWeightSet1,SimulationParameters.cooperativeWeightSet1,grid);
			Agent agent = new Agent(grid, MAX_FIRE_AGENT_SPEED, SimulationParameters.money, SimulationParameters.perceptionRange, utilityFunction, SimulationParameters.cooperativeWeightSet1,agentStatistics);
			context.add(agent);
			ra.add(grid, agent);
	    }
		
		// Randomly place agents of set 2 in grid
		int agentSet2 = SimulationParameters.agentCount - agentSet1;
		for (int i = 0; i < agentSet2; i++) {
			UtilityFunction utilityFunction = new ComponentsUtilityFunction(SimulationParameters.riskTakingWeightSet2,SimulationParameters.cooperativeWeightSet2,grid);
			Agent agent = new Agent(grid, MAX_FIRE_AGENT_SPEED, SimulationParameters.money, SimulationParameters.perceptionRange, utilityFunction, SimulationParameters.cooperativeWeightSet2,agentStatistics);
			context.add(agent);
			ra.add(grid, agent);
		}
					
		/* 
		 * Randomly place fires in grid
		 * Each of the wildfires can have a different initial speed and direction
		 * Fire starts as a 'small' fire with 1 lifepoint
		 * Fire is initalized with random direction
		 */
		for (int i = 0; i < SimulationParameters.fireCount; i++) {
			Vector2 fire_vel = new Vector2();
			// Initialize with random speed (with a maximum value 25% of the maximum fire speed) and direction
			fire_vel.x = rand.nextFloat() * ((SimulationConstants.MAX_FIRE_SPEED * 0.25f) - 0) + 0;
			fire_vel.setAngle(rand.nextFloat() * (360 - 0) + 0);
			Fire fire = new Fire(grid,fire_vel,1,SimulationParameters.lifePointsFire,SimulationParameters.fireProb);
			context.add(fire);
			ra.add(grid, fire);
		}
		
		performance.init();
		return context;
	}	
}
