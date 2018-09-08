package solution;

import problem.Box;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An algorithm for finding the best path between two points
 *
 * @param <T> The numeric system for a grid
 */
public class AStar<T extends Number> {

    // Grid coordinate information
    private final static double AREA_WIDTH = 1;
    private final static double DIVIDER = 20;
    private double nodeWidth = AREA_WIDTH / DIVIDER;

    // Starting location for the algorithm
    private Node<T> start;
    // Goal location for the algorithm
    private Node<T> goal;
    // Width of the box that is moving
    private T width;
    // Grid to traverse
    private Node<T>[][] grid;

    /**
     * Populate an A* algorithm with vital information.
     *
     * @param grid Grid to traverse
     * @param start Starting position
     * @param goal Goal position
     * @param width Width of the moving object
     */
    public AStar(Node<T>[][] grid, Point2D start, Point2D goal, T width) {
        int startNodeCol = (int) (start.getX() / nodeWidth);
        int startNodeRow = (int) (start.getY() / nodeWidth);
        int goalNodeCol = (int) (goal.getX() / nodeWidth);
        int goalNodeRow = (int) (goal.getY() / nodeWidth);

        this.start = grid[startNodeRow][startNodeCol];
        this.goal = grid[goalNodeRow][goalNodeCol];
        this.width = width;
        this.grid = grid;
    }

    /**
     * Determine the heuristic cost of moving from start to goal
     *
     * @param start Starting position
     * @param goal Goal position
     * @return Heuristic cost of movement
     */
    private int cost(Node<T> start, Node<T> goal) {
        double xDist = Math.abs(start.getX().doubleValue() - goal.getX().doubleValue());
        double yDist = Math.abs(start.getY().doubleValue() - goal.getY().doubleValue());

        int heuristic = (int) (xDist + yDist);
        Set<Node<T>> neighbours = getNeighbours(start);

        heuristic += 4 - neighbours.size();
        for (Node<T> node : neighbours) {
            if (getNeighbours(node).size() < 4) {
                heuristic += 1;
            }
        }

        return heuristic;
    }

    /**
     * Determine the set of all neighbours around a given node.
     *
     * @param node The node to search from.
     * @return All the neighbours around the node.
     */
    private Set<Node<T>> getNeighbours(Node<T> node) {
        double x = node.getX().doubleValue();
        double y = node.getY().doubleValue();

        int nodeCol = (int) (x / nodeWidth);
        int nodeRow = (int) (y / nodeWidth);
        int topCol = (int) ((x + width.doubleValue()) / nodeWidth);
        int topRow = (int) ((y + width.doubleValue()) / nodeWidth);

        Set<Node<T>> neighbours = new HashSet<>();
        if (nodeCol - 1 >= 0 && grid[nodeRow][nodeCol - 1] != null &&
                grid[nodeRow][nodeCol - 1].getBox() == null &&
                grid[topRow][topCol - 1] != null) { //move left
            neighbours.add(grid[nodeRow][nodeCol - 1]);
        }
        if (nodeRow - 1 >= 0 && grid[nodeRow - 1][nodeCol] != null &&
                grid[nodeRow - 1][nodeCol].getBox() == null &&
                grid[topRow - 1][topCol] != null) { //move down
            neighbours.add(grid[nodeRow - 1][nodeCol]);
        }
        if (topCol + 1 < grid[0].length && grid[topRow][topCol + 1] != null &&
                grid[topRow][topCol + 1].getBox() == null &&
                grid[nodeRow][nodeCol + 1] != null) { //move right
            neighbours.add(grid[nodeRow][nodeCol + 1]);
        }
        if (topRow + 1 < grid.length && grid[topRow + 1][topCol] != null &&
                grid[topRow + 1][topCol].getBox() == null &&
                grid[nodeRow + 1][nodeCol] != null) { //move up
            neighbours.add(grid[nodeRow + 1][nodeCol]);
        }

        return neighbours;
    }

    /**
     * Determine the path from the starting location to the goal location.
     *
     * @return A list of positions to reach the goal.
     */
    public List<Node<T>> run() {
        Set<Node<T>> open = new HashSet<>();
        Set<Node<T>> closed = new HashSet<>();

        start.g = 0;
        start.h = cost(start, goal);

        open.add(start);

        while (open.size() > 0) {
            Node<T> current = null;

            for (Node<T> node : open) {
                if (current == null || node.f() < current.f()) {
                    current = node;
                }
            }

            if (current.equals(goal)) {
                List<Node<T>> path = new ArrayList<>();
                while (current.parent != null) {
                    path.add(0, current);
                    current = current.parent;
                }
                path.add(0, start);

                return path;
            }

            open.remove(current);
            closed.add(current);

            for (Node<T> neighbour : getNeighbours(current)) {
                int nextG = current.g + 1;
                if (nextG < neighbour.g) {
                    open.remove(neighbour);
                    closed.remove(neighbour);
                }

                if (!open.contains(neighbour) && !closed.contains(neighbour)) {
                    neighbour.g = nextG;
                    neighbour.h = cost(neighbour, goal);
                    neighbour.parent = current;
                    open.add(neighbour);
                }
            }
        }
        return null;
    }
}
