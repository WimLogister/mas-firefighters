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
import firefighters.agent.Agent;
import firefighters.agent.SimpleAgent;


/**
 * We can set different types of weather (sunny, rainy, cloudy and windy) which result in different values for
 * velocity of the wind, quantity of rain, the chance with which fire can appear out of nowhere, etc (see code for details)
 */
public class TreeBuilder implements ContextBuilder<Object> {
	
	/*
	 * Variables influenced by the type of weather
	 */
	public double FIRE_PROB; // Chance with which fire can appear out of nowhere, depending on the weather
	public double RAIN_PROB; // Chance with which rain can appear
	public float wind_changable; // How big the variance is of the random noise added to the wind every step
	public int rain_strength; // How strong the rain is
	public Vector2 windVelocity = new Vector2(); // Wind velocity vector
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("sample-simulation");

		Random rand = new Random();
		Parameters params = RunEnvironment.getInstance().getParameters();
		int size = (Integer) params.getValue("grid_size"); // Size of the grid
		int lifePointsTree = (Integer) params.getValue("life_points_tree"); // How many steps it takes before the tree-grid has burned down completely
		int lifePointsFire = (Integer) params.getValue("life_points_fire"); // How many steps it takes before the fire has been extinguished 
		int fireCount = (Integer) params.getValue("fire_count"); // How many fires we initialize with
		int agentCount = (Integer) params.getValue("agent_count"); // How many agents we start with
		float windDirection = ((Float) params.getValue("wind_direction")); // Initial direction of wind
		String weather = (String) params.getValue("weather");		
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
				
		/*
		 * Very small chance for rain to appear, mild wind
		 */
		if(weather.equals("sunny")){
			FIRE_PROB = 0.0005; // Chance with which fire can appear out of nowhere
			RAIN_PROB = 0.002; // Chance with which new cloud can appear out of nowhere
			wind_changable = 0.05f; // Factor with which random noise is added
			rain_strength = 1; // Strengh of the rain
			windVelocity.x = (1/3)*SimulationConstants.MAX_WIND_SPEED; // Set speed of the wind			
		}		
		/*
		 * High chance for rain to appear and appear in bigger quantities, stronger wind
		 */
		else if(weather.equals("rainy")){
			FIRE_PROB = 0.00005;
			RAIN_PROB = 0.1;
			wind_changable = 0.1f;
			rain_strength = 3;
			windVelocity.x = 2*SimulationConstants.MAX_WIND_SPEED;
			
		}
		/*
		 * Higher chance for rain to appear
		 */
		else if(weather.equals("cloudy")){
			FIRE_PROB = 0.001;
			RAIN_PROB = 0.01;
			wind_changable = 0.15f;
			rain_strength = 2;
			windVelocity.x = (1/3)*SimulationConstants.MAX_WIND_SPEED;
			
		}	
		/*
		 * A strong wind with which fire and rain can appear more often
		 */
		else if(weather.equals("windy")){
			FIRE_PROB = 0.002;
			RAIN_PROB = 0.008;
			wind_changable = 0.3f;
			rain_strength = 2;
			windVelocity.x = SimulationConstants.MAX_WIND_SPEED;
			
		}	
		else throw new IllegalArgumentException("Weather-parameter must be one of the following strings: \"sunny\", \"cloudy\", \"rainy\" or \"windy\""); 
		
		windVelocity.setAngle(windDirection); // Set direction of the wind;
		Wind wind = new Wind(grid, windVelocity, wind_changable); // Add wind to the forest
		context.add(wind);
		
		// Add raincontext to the forest which will add rain given the parameters
		RainContext rc = new RainContext(grid,RAIN_PROB,size,rain_strength); 
		context.add(rc);
		
		/*
		 * Randomly place agents in grid
		 */
		for (int i = 0; i < agentCount; i++) {
			Vector2 agentVelocity = new Vector2(SimulationConstants.MAX_FIRE_AGENT_SPEED,0);			
			double money = 0;
			SimpleAgent agent = new SimpleAgent(grid, money, agentVelocity);
			context.add(agent);
			ra.add(grid, agent);
	    }
			
		/* 
		 * Randomly place fires in grid
		 * Each of the wildfires can have a different initial speed and direction
		 * Fire starts as a 'small' fire with 1 lifepoint
		 * Fire is initalized with random direction
		 */
		for (int i = 0; i < fireCount; i++) {
			Vector2 fire_vel = new Vector2();
			// Initialize with random speed (with a maximum value 25% of the maximum fire speed) and direction
			fire_vel.x = rand.nextFloat() * ((SimulationConstants.MAX_FIRE_SPEED * 0.25f) - 0) + 0;
			fire_vel.setAngle(rand.nextFloat() * (360 - 0) + 0);
			Fire fire = new Fire(grid,fire_vel,1,lifePointsFire,FIRE_PROB);
			context.add(fire);
			ra.add(grid, fire);
		}
		return context;
	}	
}
