package solution.boxes;

import java.awt.geom.Rectangle2D;

public abstract class Box {

    protected static final int DIST = 100;
    protected static final String FORMAT = "%s(x: %d, y: %d, w: %d, h: %d)";

    int x;
    int y;
    int width;
    int height;

    public Box(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Box(int x, int y, int width) {
        this(x, y, width, width);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle2D getRect() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public Rectangle2D getExpandedRect() {
        int expanded = 300;
        return new Rectangle2D.Double(x - expanded, y - expanded,
                width + 2 * expanded, height + 2 * expanded);
    }

    public Rectangle2D getBottomEdge() {
        return new Rectangle2D.Double(x, y, width, height / 2);
    }

    public Rectangle2D getTopEdge() {
        return new Rectangle2D.Double(x, y + height / 2, width, height / 2);
    }

    public Rectangle2D getLeftEdge() {
        return new Rectangle2D.Double(x, y, width / 2, height);
    }

    public Rectangle2D getRightEdge() {
        return new Rectangle2D.Double(x + width / 2, y, width / 2, height);
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
