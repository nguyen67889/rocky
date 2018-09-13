package solution.boxes;

import java.awt.geom.Rectangle2D;
import solution.Util;
import solution.states.State;

public class StaticObstacle extends Box {

    public StaticObstacle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public StaticObstacle copy() {
        return new StaticObstacle(x, y, width, height);
    }

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
