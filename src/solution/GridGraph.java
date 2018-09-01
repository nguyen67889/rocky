package solution;

import problem.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.*;

public class GridGraph {
    private final static double AREA_WIDTH = 1; //width of the area
    private final static double DIVIDER = 20; //number of nodes across
    private double nodeWidth; //width of a single node

    private Node[][] grid;

    GridGraph(ProblemSpec spec) {
        nodeWidth = AREA_WIDTH/DIVIDER;
        int numNodesHeight;
        int numNodesWidth = numNodesHeight = (int)DIVIDER;

        grid = new Node[numNodesHeight][numNodesWidth];
        for(int i = 0; i < numNodesHeight; i++) {
            for(int j = 0; j < numNodesWidth; j++) {
                double x = nodeWidth*j;
                double y = nodeWidth*i;

                grid[i][j] = new Node(null, x, y);

                for(StaticObstacle o : spec.getStaticObstacles()) {
                    Rectangle2D rect = o.getRect();
                    if(rect.intersects(x, y, nodeWidth, nodeWidth)) {
                        grid[i][j] = null;
                    }
                }

                List<Box> boxes = spec.getMovingBoxes();
                boxes.addAll(spec.getMovingObstacles());
                for(Box b : boxes) {
                    Rectangle2D rect = b.getRect();
                    if(rect.intersects(x, y, nodeWidth, nodeWidth)) {
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

    public List<Point2D> getCoordPath(List<Node> path, double width) {
        List<Point2D> result = new ArrayList<>();
        for(int i = 0; i < path.size() - 1; i++) {
            Node node = path.get(i);
            Node nextNode = path.get(i + 1);
            node.x = round(node.x, 4);
            node.y = round(node.y, 4);
            nextNode.x = round(nextNode.x, 4);
            nextNode.y = round(nextNode.y, 4);
            Point2D thisPt = new Point2D.Double(node.x + width/2, node.y + width/2);
            result.add(thisPt);
            while(thisPt.getX() < nextNode.x + width/2) {
                thisPt = new Point2D.Double(thisPt.getX() + 0.001, node.y + width/2);
                result.add(thisPt);
            }
            while(thisPt.getX() > nextNode.x + width/2) {
                thisPt = new Point2D.Double(thisPt.getX() - 0.001, node.y + width/2);
                result.add(thisPt);
            }
            while(thisPt.getY() < nextNode.y + width/2) {
                thisPt = new Point2D.Double(node.x + width/2, thisPt.getY() + 0.001);
                result.add(thisPt);
            }
            while(thisPt.getY() > nextNode.y + width/2) {
                thisPt = new Point2D.Double(node.x + width/2, thisPt.getY() - 0.001);
                result.add(thisPt);
            }
        }

        return result;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public List<Node> aStar(Box box, double xGoal, double yGoal) {
        return new AStar(box, xGoal, yGoal).run();
    }

    public static void main(String[] args) throws java.io.IOException {
        System.out.println("go!");
        ProblemSpec spec = new ProblemSpec();
        spec.loadProblem("input3.txt");
        GridGraph gg = new GridGraph(spec);
        gg.printGraph();
        Box b = spec.getMovingBoxes().get(0);
        Point2D g = spec.getMovingBoxEndPositions().get(0);
        List<Node> path = gg.aStar(b, g.getX(), g.getY());
        gg.printGraphPath(path);
        List<Point2D> coords = gg.getCoordPath(path, b.getWidth());
        System.out.println(coords);

        StringBuilder sb = new StringBuilder();
        sb.append(coords.size() + "\n");
        for(Point2D c : coords) {
            sb.append(spec.getInitialRobotConfig().getPos().getX() + " ");
            sb.append(spec.getInitialRobotConfig().getPos().getY() + " ");
            sb.append(spec.getInitialRobotConfig().getOrientation() + " ");
            sb.append(round(c.getX(), 4) + " ");
            sb.append(round(c.getY(), 4) + " ");
            sb.append(round(spec.getMovingBoxes().get(1).getPos().getX(), 4) + " ");
            sb.append(round(spec.getMovingBoxes().get(1).getPos().getY(), 4) + " ");
            sb.append(round(spec.getMovingObstacles().get(0).getPos().getX(), 4) + " ");
            sb.append(round(spec.getMovingObstacles().get(0).getPos().getY(), 4) + "\n");
        }
        System.out.println(sb.toString());
    }

    public class Node {
        private Box item;
        private double x;
        private double y;

        Node(Box item, double x, double y) {
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

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        int g;
        int h;
        Node parent;

        int f() {
            return g + h;
        }

        public String toString() {
            return "(" + (int)(x/nodeWidth) + "," + (int)(y/nodeWidth) + ")";
        }

    }

    public class AStar {
        private Box box;
        private Node start;
        private Node goal;

        public AStar(Box box, double xGoal, double yGoal) {
            this.box = box;

            double xStart = box.getPos().getX();
            double yStart = box.getPos().getY();
            int startNodeCol = (int)(xStart/nodeWidth);
            int startNodeRow = (int)(yStart/nodeWidth);
            int goalNodeCol = (int)(xGoal/nodeWidth);
            int goalNodeRow = (int)(yGoal/nodeWidth);

            start = grid[startNodeRow][startNodeCol];
            goal = grid[goalNodeRow][goalNodeCol];
        }

        private int heuristicCost(Node start, Node goal) {
            int heuristic = (int)(Math.abs(start.x - goal.x) +
                    Math.abs(start.y - goal.y));
            Set<Node> neighbours = getNeighbours(start);
            heuristic += 4 - neighbours.size();
            for(Node node : neighbours) {
                if(getNeighbours(node).size() < 4) {
                    heuristic += 1;
                }
            }
            return heuristic;
        }

        private Set<Node> getNeighbours(Node node) {
            double x = node.getX();
            double y = node.getY();
            int nodeCol = (int)(x/nodeWidth);
            int nodeRow = (int)(y/nodeWidth);
            int topCol = (int)((x + box.getWidth())/nodeWidth);
            int topRow = (int)((y + box.getWidth())/nodeWidth);

            Set<Node> neighbours = new HashSet<>();
            if(nodeCol - 1 >= 0 && grid[nodeRow][nodeCol - 1] != null) {
                neighbours.add(grid[nodeRow][nodeCol - 1]);
            }
            if(nodeRow - 1 >= 0 && grid[nodeRow - 1][nodeCol] != null) {
                neighbours.add(grid[nodeRow - 1][nodeCol]);
            }
            if(nodeCol + 1 < grid.length && grid[nodeRow][nodeCol + 1] != null &&
                    topRow < grid[0].length &&
                    topCol + 1 < grid.length && grid[topRow][topCol + 1] != null) {
                neighbours.add(grid[nodeRow][nodeCol + 1]);
            }
            if(nodeRow + 1 < grid[0].length && grid[nodeRow + 1][nodeCol] != null &&
                    topCol < grid.length &&
                    topRow + 1 < grid[0].length && grid[topRow + 1][topCol] != null) {
                neighbours.add(grid[nodeRow + 1][nodeCol]);
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
