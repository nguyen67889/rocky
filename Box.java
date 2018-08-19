import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * This class represents one of the rectangular obstacles in Assignment 1.
 * 
 * @author Sergiy Dudnikov
 */
public abstract class Box {
	/** Stores the obstacle as a Rectangle2D */
	private Rectangle2D rect;
	public Point2D pos;
	private double width;

	public Box() {

	}
	/**
	 * Constructs an obstacle with the given (x,y) coordinates of the
	 * bottom-left corner, as well as the width and height.
	 * 
	 * @param x
	 *            the minimum x-value.
	 * @param y
	 *            the minimum y-value.
	 * @param w
	 *            the width of the obstacle.
	 * @param h
	 *            the height of the obstacle.
	 */
	public Box(double x, double y, double w, double h) {
	    this.rect = new Rectangle2D.Double(x, y, w, h);
	}


	public Box(Point2D pos, double width) {
		this.pos = (Point2D) pos.clone();
		this.width = width;
//		this.isObstacle = isObstacle;
        this.rect = new Rectangle2D.Double(pos.getX(), pos.getY(), width, width);
	}

    public Point2D getPos() {
	    return pos;
    }
	/**
	 * Returns a copy of the Rectangle2D representing this obstacle.
	 * 
	 * @return a copy of the Rectangle2D representing this obstacle.
	 */
	public Rectangle2D getRect() {
		return (Rectangle2D) rect.clone();
	}

	/**
	 * Returns a String representation of this obstacle.
	 * 
	 * @return a String representation of this obstacle.
	 */
	public String toString() {
		return rect.toString();
	}
}
