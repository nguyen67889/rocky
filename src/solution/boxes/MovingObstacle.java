package solution.boxes;

import solution.Util;
import solution.states.State;

/**
 * A Movable object with no goal state
 */
public class MovingObstacle extends Movable {

    /**
     * Creates a new MovingObstacle
     * @param x the bottom left x coordinate of the MovingObstacle
     * @param y the bottom left y coordinate of the MovingObstacle
     * @param width the width of the MovingObstacle
     */
    public MovingObstacle(int x, int y, int width) {
        super(x, y, width);
    }

    /**
     * Creates a copy of the current MovingObstacle
     * @return the new MovingObstacle object
     */
    public MovingObstacle copy() {
        return new MovingObstacle(x, y, width);
    }

    /**
     * Converts a Bad Moving Obstacle(tm) to a Good Moving Obstacle(tm)
     * @param movingObstacle the Bad Moving Obstacle(tm) to convert
     * @return the new Good Moving Obstacle(tm)
     */
    public static MovingObstacle convert(problem.Box movingObstacle) {
        int area = State.AREA_SIZE;

        int x = Util.round(movingObstacle.getPos().getX() * area);
        int y = Util.round(movingObstacle.getPos().getY() * area);
        int width = Util.round(movingObstacle.getWidth() * area);

        return new MovingObstacle(x, y, width);
    }
}
