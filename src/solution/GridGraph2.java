package solution;

import problem.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridGraph2 {
    private final static double AREA_WIDTH = 1; //width of the area
    private final static double DIVIDER = 20; //number of nodes across
    private BigDecimal nodeWidth; //width of a single node

    private Node[][] grid;

    GridGraph2(ProblemSpec spec) {
        nodeWidth = Util.round(AREA_WIDTH/DIVIDER, 4);
        int numNodesHeight;
        int numNodesWidth = numNodesHeight = (int)DIVIDER;

        grid = new Node[numNodesHeight][numNodesWidth];
        for(int i = 0; i < numNodesHeight; i++) {
            for(int j = 0; j < numNodesWidth; j++) {
                BigDecimal x = nodeWidth.multiply(BigDecimal.valueOf(j));
                BigDecimal y = nodeWidth.multiply(BigDecimal.valueOf(i));

                grid[i][j] = new Node(null, x, y);

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
                        grid[i][j] = new Node(b, x, y);
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
                } else if(grid[i][j].getItem() == null) {
                    sbr.append("o ");
                } else if(grid[i][j].getItem() instanceof MovingBox) {
                    sbr.append("b ");
                } else if(grid[i][j].getItem() instanceof MovingObstacle) {
                    sbr.append("c ");
                }
            }
            sb.insert(0, sbr.toString() + "\n");
        }
        System.out.println(sb.toString());
    }

    public void printGraphPath(List<Node> path) {
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


    public List<Point2D> getCoordPath(List<Node> path, Point2D goal, Box myBox) {
        List<Point2D> result = new ArrayList<>();
        double width = myBox.getWidth();
        double x = myBox.getPos().getX() + width/2;
        double y = myBox.getPos().getY() + width/2;
        Point2D thisPt = new Point2D.Double(x, y);
        result.add(thisPt);
        path.add(new Node(null, Util.round(goal.getX(), 4), Util.round(goal.getY(), 4)));
        for(Node node : path) {

        }

        return result;
    }

    public List<Node> aStar(Box box, double xGoal, double yGoal) {
        return new AStar(box, Util.round(xGoal, 4), Util.round(yGoal, 4)).run();
    }

    public static void main(String[] args) throws java.io.IOException {
        System.out.println("go!");
        ProblemSpec spec = new ProblemSpec();
        spec.loadProblem("input.txt");

        RobotConfig init = spec.getInitialRobotConfig();
        GridGraph2 gg = new GridGraph2(spec);
        double x = init.getPos().getX();
        double y = init.getPos().getY() - 0.1;
        /*gg.printGraph();
        Box b = spec.getMovingBoxes().get(1);
        Point2D g = spec.getMovingBoxEndPositions().get(1);
        List<Node> path = gg.aStar(b, g.getX(), g.getY());
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
            sb.append(round(other.getPos().getX() + other.getWidth()/2, 4) + " ");
            sb.append(round(other.getPos().getY() + other.getWidth()/2, 4) + " ");
            sb.append(round(c.getX(), 4) + " ");
            sb.append(round(c.getY(), 4) + " ");
            sb.append(round(mobs.getPos().getX() + mobs.getWidth()/2, 4) + " ");
            sb.append(round(mobs.getPos().getY() + mobs.getWidth()/2, 4) + "\n");
        }
        System.out.println(sb.toString());*/
    }

    public class Node {
        private Box item;
        private BigDecimal x;
        private BigDecimal y;

        Node(Box item, BigDecimal x, BigDecimal y) {
            this(item);
            this.x = x;
            this.y = y;
        }

        Node(Box item) {
            this.item = item;
        }

        Box getItem() {
            return item;
        }

        BigDecimal getX() {
            return x;
        }

        BigDecimal getY() {
            return y;
        }

        int g;
        int h;
        Node parent;

        int f() {
            return g + h;
        }

        public String toString() {
            return "(" + (int)(x.divide(nodeWidth).intValue()) + "," + (int)(y.divide(nodeWidth).intValue()) + ")";
        }

    }

    public class AStar {
        private Box box;
        private Node start;
        private Node goal;

        public AStar(Box box, BigDecimal xGoal, BigDecimal yGoal) {
            this.box = box;

            BigDecimal xStart = Util.round(box.getPos().getX(), 4);
            BigDecimal yStart = Util.round(box.getPos().getY(), 4);
            int startNodeCol = xStart.divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();
            int startNodeRow = yStart.divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();
            int goalNodeCol = xGoal.divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();
            int goalNodeRow = yGoal.divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();

            start = grid[startNodeRow][startNodeCol];
            goal = grid[goalNodeRow][goalNodeCol];
        }

        private int heuristicCost(Node start, Node goal) {
            int heuristic = (int)(Math.abs(start.x.subtract(goal.x).doubleValue()) +
                    Math.abs(start.y.subtract(goal.y).doubleValue()));
            Set<Node> neighbours = getNeighbours(start);
            heuristic += 4 - neighbours.size();
            return heuristic;
        }

        private Set<Node> getNeighbours(Node node) {
            BigDecimal x = node.getX();
            BigDecimal y = node.getY();
            int nodeCol = x.divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();
            int nodeRow = y.divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();
            int topCol = x.add(Util.round(box.getWidth(), 4)).divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();
            int topRow = y.add(Util.round(box.getWidth(), 4)).divide(nodeWidth, BigDecimal.ROUND_HALF_UP).intValue();

            Node above = topRow + 1 < grid[0].length ? grid[nodeRow + 1][nodeCol] : null;
            Node below = nodeRow - 1 >= 0 ? grid[nodeRow - 1][nodeCol] : null;
            Node left = nodeCol - 1 >= 0 ? grid[nodeRow][nodeCol - 1] : null;
            Node right = topCol + 1 < grid.length ? grid[nodeRow][nodeCol + 1] : null;

            Node[] nearby = {above, below, left, right};
            Set<Node> neighbours = new HashSet<>();

            for(Node n : nearby) {
                if(n != null && n.getItem() == null) {
                    neighbours.add(n);
                }
            }

            return neighbours;
        }

        public List<Node> run() {
            Set<Node> open = new HashSet<>();
            Set<Node> closed = new HashSet<>();

            start.g = 0;
            start.h = heuristicCost(start, goal);

            open.add(start);

            while(open.size() > 0) {
                Node current = null;

                for(Node node : open) {
                    if(current == null || node.f() < current.f()) {
                        current = node;
                    }
                }

                if(current == goal) {
                    List<Node> path = new ArrayList<>();
                    while(current.parent != null) {
                        path.add(0, current);
                        current = current.parent;
                    }
                    path.add(0, start);

                    return path;
                }

                open.remove(current);
                closed.add(current);

                for(Node neighbour : getNeighbours(current)) {
                    int nextG = current.g + 1;
                    if(nextG < neighbour.g) {
                        open.remove(neighbour);
                        closed.remove(neighbour);

                    }
                    if(!open.contains(neighbour) && !closed.contains(neighbour)) {
                        neighbour.g = nextG;
                        neighbour.h = heuristicCost(neighbour, goal);
                        neighbour.parent = current;
                        open.add(neighbour);
                    }
                }
            }
            return null;
        }
    }
}
