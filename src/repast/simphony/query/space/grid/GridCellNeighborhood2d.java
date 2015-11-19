package repast.simphony.query.space.grid;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.GridPoint;

/** Grid cell neighborhood for 2d grids. Fixes a big with the repast class {@link GridCellNgh} */
public class GridCellNeighborhood2d<T> {

  private GridPoint point;
  protected Grid<? extends Object> grid;
  protected int[] extent;
  protected GridDimensions dims;
  protected int[] mins, maxs;
  protected Class<T> clazz;

  public GridCellNeighborhood2d(Grid<? extends Object> grid, GridPoint point, Class<T> clazz, int xExtent, int yExtent) {
    this.clazz = clazz;
    this.grid = grid;
    this.dims = grid.getDimensions();
    int size = dims.size();
    mins = new int[size];
    maxs = new int[size];
    this.point = point;
    setExtent(size, extent);
    setupMinMax(size);
  }

  private void setExtent(int size, int... extent) {
    if (extent == null || extent.length == 0) {
      extent = new int[size];

      for (int i = 0; i < size; i++)
        extent[i] = 1;
    }
    if (extent.length != dims.size())
      throw new IllegalArgumentException("Number of extents must" + " match the number of grid dimensions");
    this.extent = extent;
  }

  private void setupMinMax(int size) {
    for (int i = 0; i < size; i++) {
      double coord = point.getCoord(i);
      double max = coord + extent[i];
      double min = coord - extent[i];
      mins[i] = (int) min;
      maxs[i] = (int) max;
      // TODO Review
      int origin = (int) dims.getOrigin(i);
      if (min < -origin)
        min = -origin;
      int dimension = (int) dims.getDimension(i);
      if (max > dimension - origin - 1)
        max = dimension - origin - 1;
      mins[i] = (int) min;
      maxs[i] = (int) max;
    }
  }

  public List<GridCell<T>> getNeighborhood(boolean includeCenter) {
    List<GridCell<T>> list = new ArrayList<GridCell<T>>();
    for (int x = mins[0]; x <= maxs[0]; x++) {
      for (int y = mins[1]; y <= maxs[1]; y++) {
        if ((includeCenter || isNotCenter(x, y)) && isCorrectType(x, y)) {
          addCell(list, x, y);
        }
      }
    }
    return list;
  }

  private boolean isCorrectType(int x, int y) {
    for (Object o : grid.getObjectsAt(x, y)) {
      // TODO This will break with inheritance
      if (o.getClass() == clazz) {
        return true;
      }
    }
    return false;
  }

  private boolean isNotCenter(int x, int y) {
    return x != point.getX() || y != point.getY();
  }

  private void addCell(List<GridCell<T>> list, int... pt) {
    GridPoint gpt = new GridPoint(pt);
    // if (grid.isPeriodic()) {
    // grid.getGridPointTranslator().transform(gpt, pt);
    // }
    GridCell<T> cell = new GridCell<T>(gpt, clazz);
    list.add(cell);
    for (Object obj : grid.getObjectsAt(pt)) {
      cell.addObject(obj);
    }

  }

}
