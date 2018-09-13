package solution.boxes;

import java.awt.geom.Rectangle2D;
import solution.Util;
import solution.states.State;

/**
 * Represents an obstacle which cannot be moved
 */
public class StaticObstacle extends Box {

    /**
     * Creates a new StaticObstacle object
     * @param x the bottom left x coordinate of the StaticObstacle
     * @param y the bottom left y coordinate of the StaticObstacle
     * @param width the width of the StaticObstacle
     * @param height the height of the StaticObstacle
     */
    public StaticObstacle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Creates a copy of the current StaticObstacle
     * @return the new StaticObstacle object
     */
    public StaticObstacle copy() {
        return new StaticObstacle(x, y, width, height);
    }

    /**
     * Converts a Bad Static Obstacle(tm) to a Good Static Obstacle(tm)
     * @param staticObstacle the Bad Static Obstacle(tm) to be converted
     * @return the new Good Static Obstacle(tm) object
     */
    public static StaticObstacle convert(problem.StaticObstacle staticObstacle) {
        Rectangle2D rectangle = staticObstacle.getRect();
        int area = State.AREA_SIZE;

        int x = Util.round(rectangle.getX() * area);
        int y = Util.round(rectangle.getY() * area);
        int width = Util.round(rectangle.getWidth() * area);
        int height = Util.round(rectangle.getHeight() * area);

        return new StaticObstacle(x, y, width, height);
    }
}
