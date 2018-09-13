package solution.boxes;

import solution.Util;
import solution.krazysolution.State;

public class MovingObstacle extends Movable {

    public MovingObstacle(int x, int y, int width) {
        super(x, y, width);
    }

    public MovingObstacle copy() {
        return new MovingObstacle(x, y, width);
    }

    public static MovingObstacle convert(problem.Box movingObstacle) {
        int area = State.AREA_SIZE;

        int x = Util.round(movingObstacle.getPos().getX() * area);
        int y = Util.round(movingObstacle.getPos().getY() * area);
        int width = Util.round(movingObstacle.getWidth() * area);

        return new MovingObstacle(x, y, width);
    }
}
