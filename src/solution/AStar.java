package solution;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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

        // Starting heuristic of plain distance
        int heuristic = (int) (xDist + yDist);

        // Prefer to move towards nodes with less neighbours
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
     * Determine if a space in a grid is freely available.
     *
     * @param lowerPoint The lower point of the grid.
     * @param upperPoint The upper point of the grid.
     * @return True iff the space is available.
     */
    private boolean isEmptySpace(Point2D lowerPoint, Point2D upperPoint) {
        int lowerX = (int) lowerPoint.getX();
        int lowerY = (int) lowerPoint.getX();
        int upperX = (int) upperPoint.getX();
        int upperY = (int) upperPoint.getX();

        // Ensure the the box is within the grid
        boolean aboveBounds = lowerX >= 0 && lowerY >= 0;
        boolean belowBounds = upperX < grid.length && upperY < grid[0].length;

        boolean inBounds = aboveBounds && belowBounds;

        // Ensure that the space is not blocked
        boolean isNotBlocked = grid[lowerX][lowerY] != null
                && grid[upperX][upperY] != null;

        // Ensure that there isn't already a box in the space
        boolean hasNoBox = grid[lowerX][lowerY].getBox() == null
                && grid[upperX][upperY].getBox() == null;

        return inBounds && isNotBlocked && hasNoBox;
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

        // Calculate the upper and lower bounds of the node
        int nodeCol = (int) (x / nodeWidth);
        int nodeRow = (int) (y / nodeWidth);
        int topCol = (int) ((x + width.doubleValue()) / nodeWidth);
        int topRow = (int) ((y + width.doubleValue()) / nodeWidth);

        Set<Node<T>> neighbours = new HashSet<>();

        Point2D lowerPoint;
        Point2D upperPoint;

        // Loop through all the coordinates around a point
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                // Don't count itself as a neighbour
                if (xx == 0 && yy == 0) {
                    continue;
                }

                // Calculate the bounds of the new neighbour
                lowerPoint = new Double(nodeRow + xx, nodeCol + yy);
                upperPoint = new Double(topRow + xx, topCol + yy);

                // Add as neighbour if the space is empty
                if (isEmptySpace(lowerPoint, upperPoint)) {
                    neighbours.add(grid[nodeRow + xx][nodeCol + yy]);
                }
            }
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

            if (goal.equals(current)) {
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
