
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;


public class SampleFireBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		System.out.println("Context built");
		context.setId("sample-simulation");
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new RandomGridAdder<Object>(), true, 20, 20));
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		/*
		 * These lines allow us to set the initial number of trees and fires
		 * interactively in the repast UI.
		 */
		int fireCount = (Integer) params.getValue("fire_count");
		int treeCount = (Integer) params.getValue("tree_count");
		
		// Add fires to the grid
		for (int i = 0; i < fireCount; i++) {
			context.add(new SampleFire(grid));
		}
		
		// Add trees to the grid
		for (int i = 0; i < treeCount; i++) {
			context.add(new SampleTree(grid, RandomHelper.nextIntFromTo(3, 10)));
		}
		
		return context;
	}

}
