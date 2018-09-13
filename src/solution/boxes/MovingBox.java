package solution.boxes;

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
}
