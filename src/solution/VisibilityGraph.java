package solution;

import problem.Box;
import problem.ProblemSpec;
import problem.RobotConfig;
import problem.StaticObstacle;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisibilityGraph {

    private List<Node> nodes;
    private Node start;

    private void addNodes(List<Node> nodes, double x, double y, double width, double height) {
        BigDecimal bottomLeftX, topLeftX;
        bottomLeftX = topLeftX = Util.round(x - 0.001, 4);
        BigDecimal bottomLeftY, bottomRightY;
        bottomLeftY = bottomRightY = Util.round(y - 0.001, 4);
        BigDecimal bottomRightX, topRightX;
        bottomRightX = topRightX = Util.round(x + width + 0.001, 4);
        BigDecimal topLeftY, topRightY;
        topLeftY = topRightY = Util.round(y + height + 0.001, 4);

        nodes.add(new Node(bottomLeftX, bottomLeftY));
        nodes.add(new Node(bottomRightX, bottomRightY));
        nodes.add(new Node(topLeftX, topLeftY));
        nodes.add(new Node(topRightX, topRightY));
    }

    private boolean intersects(Line2D line, List<Rectangle2D> rects) {
        for(Rectangle2D rect : rects) {
            if(line.intersects(rect)) {
                return true;
            }
        }
        return false;
    }

    public VisibilityGraph(RobotConfig robot, List<Box> boxes, List<StaticObstacle> obstacles) {
        nodes = new ArrayList<>();
        List<Rectangle2D> rects = new ArrayList<>();

        start = new Node(Util.round(robot.getPos().getX(), 4),
                Util.round(robot.getPos().getY(), 4));
        nodes.add(start);

        for(Box box : boxes) {
            addNodes(nodes, box.getPos().getX(), box.getPos().getY(), box.getWidth(), box.getWidth());
            rects.add(box.getRect());
        }
        for(StaticObstacle obs : obstacles) {
            addNodes(nodes, obs.getRect().getX(), obs.getRect().getY(),
                    obs.getRect().getWidth(), obs.getRect().getHeight());
            rects.add(obs.getRect());
        }


         for(Node start : nodes) {
            for(Node end : nodes) {
                if(start != end) {
                    Edge edge = new Edge(start, end);
                    if(!intersects(edge.line, rects)) {
                        start.edges.add(edge);
                        end.edges.add(edge);
                    }
                }
            }
         }
    }

    public List<RobotConfig> genPoints(List<AbstractSearch.AbstractNode> nodes, RobotConfig start) {
        List<RobotConfig> configs = new ArrayList<>();
        configs.add(start);

        for(int i = 0; i < nodes.size(); i++) {
            if(i == nodes.size() - 1) {
                break;
            }
            Node node = (Node)nodes.get(i);
            Node nextNode = (Node)nodes.get(i + 1);
            Edge followEdge = null;
            for(Edge edge : node.edges) {
                if((edge.start == node && edge.end == nextNode) || (edge.start == nextNode && edge.end == node)) {
                    followEdge = edge;
                    break;
                }
            }
            BigDecimal angle = Util.round(Math.atan2(followEdge.line.getY1() - followEdge.line.getY2(),
                    followEdge.line.getX1() - followEdge.line.getX2()), 4);
            BigDecimal yChange = nextNode.y.subtract(node.y);
            BigDecimal xChange = nextNode.x.subtract(node.x);
            Path2D p = new Path2D.Double();
            p.append(followEdge.line, true);

            for(BigDecimal turn : Util.rotationAngles(Util.round(start.getOrientation(), 4), angle)) {
                start = new RobotConfig(start.getPos(), turn.doubleValue());
                configs.add(start);
            }

            while(!Util.round(start.getPos().getX(), 4).equals(nextNode.x) ||
                    !Util.round(start.getPos().getY(), 4).equals(nextNode.y)) {
                start = new RobotConfig(new Point2D.Double(start.getPos().getX() +
                        Util.round(0.001*Math.cos(angle.doubleValue()), 4).doubleValue(),
                        start.getPos().getY() +
                                Util.round(0.001*Math.sin(angle.doubleValue()), 4).doubleValue()),
                        start.getOrientation());
                configs.add(start);
            }
        }
        System.out.println(configs);
        return configs;
    }

    public List<AbstractSearch.AbstractNode> aStar(Box goal) {
        BigDecimal x = Util.round(goal.getPos().getX() - 0.001, 4);
        BigDecimal y = Util.round(goal.getPos().getX() - 0.001, 4);

        Node goalNode = null;
        for(Node node : nodes) {
            if(node.x.equals(x) && node.y.equals(y)) {
                goalNode = node;
                break;
            }
        }

        return new AStarVG(start, goalNode).run();
    }

    public static void main(String[] args) throws java.io.IOException {
        System.out.println("go!");
        ProblemSpec spec = new ProblemSpec();
        spec.loadProblem("problems/input.txt");

        List<Box> boxes = spec.getMovingBoxes();
        boxes.addAll(spec.getMovingObstacles());
        VisibilityGraph vg = new VisibilityGraph(spec.getInitialRobotConfig(), boxes,
                spec.getStaticObstacles());
        List<AbstractSearch.AbstractNode> path = vg.aStar(boxes.get(0));
        vg.genPoints(path, spec.getInitialRobotConfig());
    }

    public class Edge {
        Node start;
        Node end;
        Line2D line;

        public Edge(Node start, Node end) {
            this.start = start;
            this.end = end;

            line = new Line2D.Double(start.x.doubleValue(), start.y.doubleValue(),
                    end.x.doubleValue(), end.y.doubleValue());
        }
    }

    public class Node extends AbstractSearch.AbstractNode {
        BigDecimal x;
        BigDecimal y;
        List<Edge> edges;

        public Node(BigDecimal x, BigDecimal y) {
            this.x = x;
            this.y = y;
            edges = new ArrayList<>();
        }

        public String toString() {
            return String.format("(%f, %f)", x.doubleValue(), y.doubleValue());
        }
    }

    private class AStarVG extends AbstractSearch {

        public AStarVG(AbstractNode start, AbstractNode goal) {
            super(start, goal);
        }

        @Override
        protected int heuristicCost(AbstractNode start, AbstractNode goal) {
            Node startNode = (Node)start;
            Node goalNode = (Node)goal;
            return (int)(Math.abs(startNode.x.subtract(goalNode.x).doubleValue()) +
                    Math.abs(startNode.y.subtract(goalNode.y).doubleValue()));
        }

        @Override
        protected Set<AbstractNode> getNeighbours(AbstractNode node) {
            List<Edge> edges = ((Node)node).edges;
            Set<AbstractNode> neighbours = new HashSet<>();
            for(Edge edge : edges) {
                Node other;
                if(edge.start == node) {
                    other = edge.end;
                } else {
                    other = edge.start;
                }
                neighbours.add(other);
            }
            return neighbours;
        }
    }
}
