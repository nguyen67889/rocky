package solution.boxes;

import java.awt.geom.Rectangle2D;

/**
 * Represents a box
 */
public abstract class Box {

    // the maximum primitive movement step a box can make
    protected static final int DIST = 100;
    // format for the toString
    protected static final String FORMAT = "%s(x: %d, y: %d, w: %d, h: %d)";

    // the coordinates and dimensions of the Box
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    /**
     * Creates a new square Box object
     * @param x x coordinate of the bottom left corner
     * @param y y coordinate the bottom left corner
     * @param width width of the box
     */
    public Box(int x, int y, int width) {
        this(x, y, width, width);
    }

    /**
     * Creates a new rectangle Box object
     * @param x x coordinate of the bottom left corner
     * @param y y coordinate of the bottom left corner
     * @param width width of the box
     * @param height height of the box
     */
    public Box(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the x coordinate of the Box
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of the Box
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the width of the Box
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the Box
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Creates a Rectangle2D representation of the Box using its coordinates,
     * width, and height
     * @return the Rectangle2D object
     */
    public Rectangle2D getRect() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    /**
     * Expands the Box by 300 on all sides
     * @return the expanded Rectangle2D object
     */
    public Rectangle2D getExpandedRect() {
        int expanded = 300;
        return new Rectangle2D.Double(x - expanded, y - expanded,
                width + 2 * expanded, height + 2 * expanded);
    }

    public abstract Box copy();

    public boolean equals(Object obj) {
        if (!(obj instanceof Box)) {
            return false;
        }
        Box box = (Box) obj;
        boolean sameSize = box.width == width && box.height == height;

        return box.x == x && box.y == y && sameSize;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        hash = 31 * hash + width;
        hash = 31 * hash + height;
        return hash;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, this.getClass().getName(), x, y, width, height);
    }
}
