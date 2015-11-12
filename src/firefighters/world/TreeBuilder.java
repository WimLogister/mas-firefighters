package firefighters.world;


import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import constants.SimulationConstants;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import cern.jet.random.Uniform;
import firefighters.agent.SimpleAgent;


/**
 * We can set different types of weather (sunny, ...) which result in different values for
 * velocity of the wind, quantity of rain
 */
public class TreeBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		context.setId("sample-simulation");

		Random rand = new Random();
		Parameters params = RunEnvironment.getInstance().getParameters();
		int size = (Integer) params.getValue("grid_size");
		int lifePointsTree = (Integer) params.getValue("life_points_tree"); // How many steps it takes before the tree-grid has burned down completely
		int lifePointsFire = (Integer) params.getValue("life_points_fire"); // How many steps it takes before the fire has been extinguished 
		int fireCount = (Integer) params.getValue("fire_count"); // How many fires we initialize with
		//int rainCount = (Integer) params.getValue("rain_count"); // How much rain we initialize with
		int agentCount = (Integer) params.getValue("agent_count"); // How many agents we start with
		//double maxSpeedWind = (Double) params.getValue("max_speed_wind"); // The maximum speed of the wind
		float windDirection = ((Float) params.getValue("wind_direction")); // Initial direction of wind
		String weather = (String) params.getValue("weather");
		
		Vector2 windVelocity = new Vector2();
		
		// No rain at all, mild wind
		if(weather.equals("sunny")){
			SimulationConstants.RAIN_PROB = 0.01;
			windVelocity.x = 1;
			windVelocity.setAngle(windDirection);
		}
		else throw new IllegalArgumentException("Weather-parameter must be one of the following strings: \"sunny\", \"cloudy\", \"rainy\" or \"windy\""); 
		
		final Uniform urng = RandomHelper.getUniform();
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, size, size)); // Square grid, variable size
		
		GridDimensions dims = grid.getDimensions();
		RandomGridAdder<Object> ra = new RandomGridAdder<Object>(); // To random add objects in the space
		
		// Fill the grid completely with trees
		for (int d0=0; d0<dims.getDimension(0); d0++){
			for (int d1=0; d1<dims.getDimension(1); d1++){
				int[] nextLoc = {d0,d1};
				Tree tree = new Tree(grid,lifePointsTree);
				context.add(tree);
				grid.moveTo(tree, nextLoc);
			}
		}
		
		Wind wind = new Wind(grid, windVelocity); // Add wind to the forest
		context.add(wind);
		
		/*
		 * Randomly place agents in grid or place agents in the center of the forest
		 */
		
		
		
		/* 
		 * Randomly place fires in grid
		 * Each of the wildfires can have a different initial speed and direction
		 * Fire starts as a 'small' fire with 1 lifepoint
		 * Fire is initalized with random direction
		 */
		/*for (int i = 0; i < fireCount; i++) {
			Vector2 fire_vel = new Vector2();
			fire_vel.x = rand.nextFloat() * (SimulationConstants.MAX_FIRE_SPEED - 0) + 0;
			fire_vel.setAngle(rand.nextFloat() * (360 - 0) + 0);
			Fire fire = new Fire(grid,fire_vel,1,lifePointsFire);
			context.add(fire);
			ra.add(grid, fire);
		}*/
		
		
		Vector2 fire_vel = new Vector2();
		fire_vel.x = rand.nextFloat() * (SimulationConstants.MAX_FIRE_SPEED - 0) + 0;
		fire_vel.setAngle(135);
		Fire fire = new Fire(grid,fire_vel,1,lifePointsFire);
		context.add(fire);
		int[] nextLoc = {5,5};
		grid.moveTo(fire, nextLoc);
	
		Rain rain = new Rain(grid,windVelocity);
		context.add(rain);
		int[] n1 = {5,6};
		grid.moveTo(rain,n1);


		Rain rain2 = new Rain(grid,windVelocity);
		context.add(rain2);
		int[] n2 = {4,6};
		grid.moveTo(rain2,n2);
		
		
		Rain rain3 = new Rain(grid,windVelocity);
		context.add(rain3);
		int[] n3 = {4,5};
		grid.moveTo(rain3,n3);
		
		// Randomly place rain in grid
		/*for (int i = 0; i < rainCount; i++) {
			Rain rain = new Rain(grid);
			context.add(rain);
			ra.add(grid, rain);
		}*/

    // Randomly place the agents
    /*for (int i = 0; i < agentCount; i++) {
      double movementSpeed = 1.0;
      double money = 0;
      SimpleAgent agent = new SimpleAgent(grid, movementSpeed, money);
      context.add(agent);
      ra.add(grid, agent);
    }*/
		return context;
	}	
}
