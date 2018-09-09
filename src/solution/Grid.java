package solution;

import problem.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A 2D discretized grid of boxes.
 *
 * @param <T> The numeric system for a grid
 */
public class Grid<T extends Number> {

    // Grid coordinate information
    private final static double AREA_WIDTH = 1;
    private final static double DIVIDER = 20;
    private BigDecimal nodeWidth;

    private Node[][] grid;

    public Grid(ProblemSpec spec) {
        nodeWidth = Util.round(AREA_WIDTH/DIVIDER, 4);
        int numNodesHeight;
        int numNodesWidth = numNodesHeight = (int)DIVIDER;

        grid = new Node[numNodesHeight][numNodesWidth];
        for(int i = 0; i < numNodesHeight; i++) {
            for(int j = 0; j < numNodesWidth; j++) {
                BigDecimal x = nodeWidth.multiply(BigDecimal.valueOf(j));
                BigDecimal y = nodeWidth.multiply(BigDecimal.valueOf(i));

                grid[i][j] = new Node<>(null, x, y);

                for(StaticObstacle o : spec.getStaticObstacles()) {
                    Rectangle2D rect = o.getRect();
                    if(rect.intersects(x.doubleValue(), y.doubleValue(),
                            nodeWidth.doubleValue(), nodeWidth.doubleValue())) {
                        grid[i][j] = null;
                    }
                }

                List<Box> boxes = new ArrayList<>(spec.getMovingBoxes());
                boxes.addAll(spec.getMovingObstacles());
                for(Box b : boxes) {
                    Rectangle2D rect = b.getRect();
                    if(rect.intersects(x.doubleValue(), y.doubleValue(), nodeWidth.doubleValue(), nodeWidth.doubleValue())) {
                        grid[i][j] = new Node<>(b, x, y);
                    }
                }
            }
        }
    }

    public Node<T>[][] getGrid() {
        return grid;
    }

    public void moveBox(Box box, Point2D position) {
        int width = (int) DIVIDER;

        Box newBox = null;
        try {
            newBox = box.getClass()
                    .getConstructor(Point2D.class, double.class)
                    .newInstance(position, box.getWidth());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                double xPoint = nodeWidth.doubleValue() * j;
                double yPoint = nodeWidth.doubleValue() * i;

                if (newBox.getRect().intersects(xPoint, yPoint,
                        nodeWidth.doubleValue(), nodeWidth.doubleValue())) {
                    grid[i][j] = new Node<>(box, BigDecimal.valueOf(xPoint),
                            BigDecimal.valueOf(yPoint));
                }
            }
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        int size = (int) DIVIDER;

        for (int i = 0; i < size; i++) {
            StringBuilder sbr = new StringBuilder();

            for (int j = 0; j < size; j++) {
                if (grid[i][j] == null) {
                    sbr.append("  ");
                } else if (grid[i][j].getBox() == null) {
                    sbr.append("o ");
                } else if (grid[i][j].getBox() instanceof MovingBox) {
                    sbr.append("b ");
                } else if (grid[i][j].getBox() instanceof MovingObstacle) {
                    sbr.append("c ");
                }
            }
            result.insert(0, sbr.toString() + "\n");
        }

        return result.toString();
    }

    public List<Point2D> getCoordPath(List<Node<T>> path, Point2D goal, Box myBox) {
        List<Point2D> result = new ArrayList<>();
        double width = myBox.getWidth();
        double x = myBox.getPos().getX() + width/2;
        double y = myBox.getPos().getY() + width/2;
        Point2D thisPt = new Point2D.Double(x, y);
        result.add(thisPt);
        path.add(new Node(null, Util.round(goal.getX(), 4), Util.round(goal.getY(), 4)));
        for(Node node : path) {
            while(thisPt.getX() < node.getX().doubleValue() + width/2) {
                thisPt = new Point2D.Double(thisPt.getX() + 0.001, thisPt.getY());
                result.add(thisPt);
            }
            while(thisPt.getX() > node.getX().doubleValue() + width/2) {
                thisPt = new Point2D.Double(thisPt.getX() - 0.001, thisPt.getY());
                result.add(thisPt);
            }
            while(thisPt.getY() < node.getY().doubleValue() + width/2) {
                thisPt = new Point2D.Double(thisPt.getX(), thisPt.getY() + 0.001);
                result.add(thisPt);
            }
            while(thisPt.getY() > node.getY().doubleValue() + width/2) {
                thisPt = new Point2D.Double(thisPt.getX(), thisPt.getY() - 0.001);
                result.add(thisPt);
            }
        }

        return result;
    }
}
