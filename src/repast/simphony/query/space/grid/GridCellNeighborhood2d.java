package repast.simphony.query.space.grid;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/** Grid cell neighborhood for 2d grids. Fixes a big with the repast class {@link GridCellNgh} */
public class GridCellNeighborhood2d<T>
    extends GridCellNgh<T> {

  private GridPoint point;
  private boolean wrapAround;

  public GridCellNeighborhood2d(Grid<? extends Object> grid,
                                GridPoint point,
                                Class<T> clazz,
                                int xExtent,
                                int yExtent,
                                boolean wrapAround) {
    super(grid, point, clazz, xExtent, yExtent);
    this.point = point;
    this.wrapAround = wrapAround;
    setupMinMax(2);
  }

  @Override
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

  private void setupMinMax(int size) {
    for (int i = 0; i < size; i++) {
      double coord = point.getCoord(i);
      double max = coord + extent[i];
      double min = coord - extent[i];
      mins[i] = (int) min;
      maxs[i] = (int) max;
      if (!wrapAround) {
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
    if (grid.isPeriodic()) {
      grid.getGridPointTranslator().transform(gpt, pt);
    }
    GridCell<T> cell = new GridCell<T>(gpt, clazz);
    list.add(cell);
    for (Object obj : grid.getObjectsAt(pt)) {
      cell.addObject(obj);
    }

  }

}
