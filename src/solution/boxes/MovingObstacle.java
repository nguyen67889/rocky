package solution.boxes;

public class MovingObstacle extends MovingBox {
    public MovingObstacle(int x, int y, int width) {
        super(x, y, x, y, width);
    }

    public MovingObstacle copy() {
        return new MovingObstacle(x, y, width);
    }
}
