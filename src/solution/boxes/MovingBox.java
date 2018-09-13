package solution.boxes;

import java.awt.geom.Point2D;
import solution.Util;
import solution.states.State;

public class MovingBox extends Movable {

    private int xGoal;
    private int yGoal;

    public MovingBox(int x, int y, int xGoal, int yGoal, int width) {
        super(x, y, width);
        this.xGoal = xGoal;
        this.yGoal = yGoal;
    }

    public int getXGoal() {
        return xGoal;
    }

    public int getYGoal() {
        return yGoal;
    }

    public MovingBox copy() {
        return new MovingBox(x, y, xGoal, yGoal, width);
    }

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
