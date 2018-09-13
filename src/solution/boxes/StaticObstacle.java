package solution.boxes;

public class StaticObstacle extends Box {

    public StaticObstacle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public StaticObstacle copy() {
        return new StaticObstacle(x, y, width, height);
    }
}
