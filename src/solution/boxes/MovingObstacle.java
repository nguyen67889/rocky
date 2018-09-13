package solution.boxes;

public class MovingObstacle extends Movable {

    public MovingObstacle(int x, int y, int width) {
        super(x, y, width);
    }

    public MovingObstacle copy() {
        return new MovingObstacle(x, y, width);
    }
}
