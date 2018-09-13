package solution.boxes;

/**
 * Represents a movable box (either a moving box or an obstacle)
 */
public abstract class Movable extends Box {

    /**
     * Constructs a new square Movable object
     * @param x x coordinate of the bottom left corner of the object
     * @param y y coordinate of the bottom left corner of the object
     * @param width the width of the object
     */
    public Movable(int x, int y, int width) {
        super(x, y, width);
    }

    /**
     * Constructs a new rectangular Movable object
     * @param x x coordinate of the bottom left corner of the object
     * @param y y coordinate of the bottom left corner of the object
     * @param width the width of the object
     * @param height the height of the object
     */
    public Movable(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Sets the x coordinate
     * @param x the new x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate
     * @param y the new y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Moves the movable object to the given coordinate
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Moves the movable object up by a set distance
     */
    public void moveUp() {
        y += DIST;
    }

    /**
     * Moves the movable object down by a set distance
     */
    public void moveDown() {
        y -= DIST;
    }

    /**
     * Moves the movable object left by a set distance
     */
    public void moveLeft() {
        x -= DIST;
    }

    /**
     * Moves the movable object right by a set distance
     */
    public void moveRight() {
        x += DIST;
    }

}
