package solution.boxes;

import java.awt.geom.Point2D;
import solution.Util;
import solution.states.State;

/**
 * A Movable object that has a goal state associated with it
 */
public class MovingBox extends Movable {

    // coordinates of the goal position
    private int xGoal;
    private int yGoal;

    /**
     * Creates a new MovingBox object
     * @param x bottom left x coordinate of MovingBox
     * @param y bottom left y coordinate of MovingBox
     * @param xGoal bottom left x coordinate of goal state
     * @param yGoal bottom left y coordinate of goal state
     * @param width the width of the MovingBox
     */
    public MovingBox(int x, int y, int xGoal, int yGoal, int width) {
        super(x, y, width);
        this.xGoal = xGoal;
        this.yGoal = yGoal;
    }

    /**
     * Gets the x coordinate of the goal state
     * @return the x coordinate
     */
    public int getXGoal() {
        return xGoal;
    }

    /**
     * Gets the y coordinate of the goal state
     * @return the y coordinate
     */
    public int getYGoal() {
        return yGoal;
    }

    /**
     * Creates a copy of the current MovingBox
     * @return the copied MovingBox
     */
    public MovingBox copy() {
        return new MovingBox(x, y, xGoal, yGoal, width);
    }

    /**
     * Converts a Bad Box(tm) to a Good Box(tm)
     * @param box the Bad Box(tm) to be converted
     * @param goal the goal of the Box being converted
     * @return the new Good Box(tm)
     */
    public static MovingBox convert(problem.Box box, Point2D goal) {
        int area = State.AREA_SIZE;

        int x = Util.round(box.getPos().getX() * area);
        int y = Util.round(box.getPos().getY() * area);
        int width = Util.round(box.getWidth() * area);
        int goalX = Util.round(goal.getX() * area);
        int goalY = Util.round(goal.getY() * area);

        return new MovingBox(x, y, goalX, goalY, width);
    }
}
