package firefighters.world;


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
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;


public class TreeBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		// TODO: Grid variable size 
		
		context.setId("sample-simulation");
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int Width = (Integer) params.getValue("width");
		int Height = (Integer) params.getValue("heigth");
		int lifePoints = (Integer) params.getValue("life_points"); // How many steps it takes before the tree-grid has burned down completely
		int fireCount = (Integer) params.getValue("fire_count");
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, 100, 100));
		
		GridDimensions dims = grid.getDimensions();
		
		
		
		//Add fires to the grid
		for (int i = 0; i < fireCount; i++) {
			context.add(new Fire(grid));
		}
		
		// Add trees to the grid
		for (int d0=0; d0<dims.getDimension(0); d0++){
			for (int d1=0; d1<dims.getDimension(1); d1++){
				int[] nextLoc = {d0,d1};
				Tree tree = new Tree(grid,lifePoints);
				context.add(tree);
				grid.moveTo(tree, nextLoc);
			}
		}
		
		return context;
	}

}
