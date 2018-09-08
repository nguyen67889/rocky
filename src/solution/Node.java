package solution;

import problem.Box;

/**
 * A point in the grid.
 *
 * The point can be empty or have a box.
 *
 * @param <T> Numeric points of the grid
 */
public class Node<T extends Number> {
    // The box at the point, null if empty
    private Box box;

    // Coordinates of the point
    private T x;
    private T y;

    // Information about the point needed for A* algorithm
    public int g;
    public int h;
    public Node<T> parent;

    /**
     * Construct a new point in the grid.
     *
     * @param box The box at the point
     * @param x X location of point
     * @param y Y location of point
     */
    public Node(Box box, T x, T y) {
        this.box = box;
        this.x = x;
        this.y = y;
    }

    /**
     * Find the box stored at this point.
     *
     * @return The box at this point or null if point is empty.
     */
    public Box getBox() {
        return box;
    }

    /**
     * @return X location of point
     */
    T getX() {
        return x;
    }

    /**
     * @return Y location of point
     */
    T getY() {
        return y;
    }


    int f() {
        return g + h;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }

        Node other = (Node) obj;
        return x.intValue() == other.x.intValue()
                && y.intValue() == other.y.intValue();
    }

    public String toString() {
        return getBox() != null ? getBox().toString() : null;
        //return "(" + (int)(x/nodeWidth) + "," + (int)(y/nodeWidth) + ")";
    }
}
