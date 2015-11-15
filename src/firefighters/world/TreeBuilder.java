package firefighters.world;


import static constants.SimulationConstants.AGENT_PERCEPTION_DISTANCE;
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
import firefighters.utils.Directions;


/*
 * TODO:
 * Maybe better to start "groups" of fire or one bigger fire instead of randomly scattered single fires?
 * Visualize wind in grid
 */
public class TreeBuilder implements ContextBuilder<Object> {

  @Override
	public Context build(Context<Object> context) {
		context.setId("sample-simulation");

		Parameters params = RunEnvironment.getInstance().getParameters();
		int size = (Integer) params.getValue("grid_size");
		int lifePointsTree = (Integer) params.getValue("life_points_tree"); // How many steps it takes before the tree-grid has burned down completely
		int lifePointsFire = (Integer) params.getValue("life_points_fire");
		int fireCount = (Integer) params.getValue("fire_count"); // How many fires we initialize with
		int rainCount = (Integer) params.getValue("rain_count"); // How much rain we initialize with
		int agentCount = (Integer) params.getValue("agent_count"); // How many agents we start with
		Directions windDirection = returnWindDirection((String) params.getValue("wind_direction")); // Initial direction of wind
		
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
		
		Wind wind = new Wind(grid, windDirection); // Add wind to the forest
		context.add(wind);
		
		/*
		 * Randomly place agents in grid: constructor agent?
		 */
		
		
		
		/* 
		 * Randomly place fires in grid
		 * Each of the wildfires can have a different initial speed and direction
		 */
		for (int i = 0; i < fireCount; i++) {
			Fire fire = new Fire(grid,Directions.getRandomDirection(),urng.nextDouble(),lifePointsFire,lifePointsFire);
			context.add(fire);
			ra.add(grid, fire);
		}
		
		// Randomly place rain in grid
		for (int i = 0; i < rainCount; i++) {
			Rain rain = new Rain(grid);
			context.add(rain);
			ra.add(grid, rain);
		}

    // Randomly place the agents
    for (int i = 0; i < agentCount; i++) {
      double movementSpeed = 1.0;
      double money = 0;
      Agent agent = new Agent(grid, movementSpeed, money, AGENT_PERCEPTION_DISTANCE);
      context.add(agent);
      ra.add(grid, agent);
    }
		return context;
	}
	
	public Directions returnWindDirection(String string){
		if(string.equals("north")) return Directions.NORTH;
		else if(string.equals("south")) return Directions.SOUTH;
		else if(string.equals("east")) return Directions.EAST;
		else if(string.equals("west")) return Directions.WEST;
		else throw new IllegalArgumentException("Wind direction must be one of the following strings: \"north\", \"south\", \"east\" or \"west\"");
	}
}
