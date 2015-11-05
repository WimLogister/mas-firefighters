package firefighters.world;


import cern.jet.random.Uniform;
import firefighters.utils.Directions;
import firefighters.utils.SimpleGridAdderExtended;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;


public class TreeBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		context.setId("sample-simulation");
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int size = (Integer) params.getValue("grid_size");
		int lifePoints = (Integer) params.getValue("life_points"); // How many steps it takes before the tree-grid has burned down completely
		int fireCount = (Integer) params.getValue("fire_count"); // How many fires we initialize with
		int rainCount = (Integer) params.getValue("rain_count");
		final Uniform urng = RandomHelper.getUniform();
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdderExtended<Object>(), true, size, size)); // Square grid, variable size
		
		GridDimensions dims = grid.getDimensions();
		
		// Fill the grid with trees
		for (int d0=0; d0<dims.getDimension(0); d0++){
			for (int d1=0; d1<dims.getDimension(1); d1++){
				int[] nextLoc = {d0,d1};
				Tree tree = new Tree(grid,lifePoints);
				context.add(tree);
				grid.moveTo(tree, nextLoc);
			}
		}
		
		// Add wind to the forest
		// TODO: How to visualize wind in grid?
		// TODO: Add initial wind direction as parameter in the model?
		Wind wind = new Wind(grid, Directions.getRandomDirection());
		
		/* 
		 * Randomly place fires in grid
		 * Each of the wildfires can have a different initial speed and direction
		 * Is there a way to use SimpleGridAdder and RandomCartesianAdder in the same grid?
		 * Maybe better to start "groups" of fire or one bigger fire instead of randomly scattered single fires?
		 * TODO: check if there already is a fire initialized in the new random place.
		 */
		for (int i = 0; i < fireCount; i++) {
			// TODO: AddRandom
			int[] nextLoc = {RandomHelper.nextIntFromTo(0,dims.getDimension(0)),RandomHelper.nextIntFromTo(0,dims.getDimension(1))};
			// Initialize with random direction and speed
			Fire fire = new Fire(grid,Directions.getRandomDirection(),urng.nextDouble());
			context.add(fire);
			grid.moveTo(fire, nextLoc);	
		}
		
		/*
		 * Randomly place rain in grid
		 */
		for (int i = 0; i < rainCount; i++) {
			// TODO: AddRandom
			int[] nextLoc = {RandomHelper.nextIntFromTo(0,dims.getDimension(0)),RandomHelper.nextIntFromTo(0,dims.getDimension(1))};
			// Initialize with random direction and speed
			Rain rain = new Rain(grid);
			context.add(rain);
			grid.moveTo(rain, nextLoc);	
		}
		return context;
	}
}
