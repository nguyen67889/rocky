package solution;

import problem.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Grid<T extends Number> {
    private final static double AREA_WIDTH = 1; //width of the area
    private final static double DIVIDER = 20; //number of nodes across
    private BigDecimal nodeWidth; //width of a single node

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

                List<Box> boxes = spec.getMovingBoxes();
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

    public void printGraph() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < (int)DIVIDER; i++) {
            StringBuilder sbr = new StringBuilder();
            for(int j = 0; j < (int)DIVIDER; j++) {
                if(grid[i][j] == null) {
                    sbr.append("  ");
                } else if(grid[i][j].getBox() == null) {
                    sbr.append("o ");
                } else if(grid[i][j].getBox() instanceof MovingBox) {
                    sbr.append("b ");
                } else if(grid[i][j].getBox() instanceof MovingObstacle) {
                    sbr.append("c ");
                }
            }
            sb.insert(0, sbr.toString() + "\n");
        }
        System.out.println(sb.toString());
    }

    public void printGraphPath(List<Node<T>> path) {
        if(path == null) {
            throw new RuntimeException("Path is null");
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < (int)DIVIDER; i++) {
            StringBuilder sbr = new StringBuilder();
            for(int j = 0; j < (int)DIVIDER; j++) {
                if(grid[i][j] == null) {
                    sbr.append("  ");
                } else if(path.contains(grid[i][j])) {
                    sbr.append("x ");
                } else {
                    sbr.append("o ");
                }
            }
            sb.insert(0, sbr.toString() + "\n");
        }
        System.out.println(sb.toString());
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

    public static void main(String[] args) throws java.io.IOException {
        ProblemSpec spec = new ProblemSpec();
        spec.loadProblem("problems/input.txt");

        Grid gg = new Grid<BigDecimal>(spec);
        gg.printGraph();

        Box b = spec.getMovingBoxes().get(1);
        Point2D g = spec.getMovingBoxEndPositions().get(1);
        BigDecimal width = new BigDecimal(b.getWidth());

        AStar<BigDecimal> aStar = new AStar<BigDecimal>(gg.grid, b.getPos(), g, width);
        List<Node<BigDecimal>> path = aStar.run();

        gg.printGraphPath(path);
        List<Point2D> coords = gg.getCoordPath(path, g, b);
        System.out.println(coords);

        StringBuilder sb = new StringBuilder();
        sb.append(coords.size() + "\n");
        for(Point2D c : coords) {
            Box other = spec.getMovingBoxes().get(0);
            Box mobs = spec.getMovingObstacles().get(0);
            sb.append(spec.getInitialRobotConfig().getPos().getX() + " ");
            sb.append(spec.getInitialRobotConfig().getPos().getY() + " ");
            sb.append(spec.getInitialRobotConfig().getOrientation() + " ");
            sb.append(Util.round(other.getPos().getX() + other.getWidth()/2, 4) + " ");
            sb.append(Util.round(other.getPos().getY() + other.getWidth()/2, 4) + " ");
            sb.append(Util.round(c.getX(), 4) + " ");
            sb.append(Util.round(c.getY(), 4) + " ");
            sb.append(Util.round(mobs.getPos().getX() + mobs.getWidth()/2, 4) + " ");
            sb.append(Util.round(mobs.getPos().getY() + mobs.getWidth()/2, 4) + "\n");
        }
        System.out.println(sb.toString());
    }
}
