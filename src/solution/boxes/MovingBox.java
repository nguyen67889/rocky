package solution.boxes;

public class MovingBox extends Box {

    private int xGoal;
    private int yGoal;

    public MovingBox(int x, int y, int xGoal, int yGoal, int width) {
        super(x, y, width);
        this.xGoal = xGoal;
        this.yGoal = yGoal;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveUp() {
        y += DIST;
    }

    public void moveDown() {
        y -= DIST;
    }

    public void moveLeft() {
        x -= DIST;
    }

    public void moveRight() {
        x += DIST;
    }

    public int getXGoal() {
        return xGoal;
    }

    public int getYGoal() {
        return yGoal;
    }

    public MovingBox gridifyGoal() {
        int boxX = x;
        int boxY = y;

        while (boxX <= xGoal - DIST) {
            boxX += DIST;
        }
        while (boxX >= xGoal + DIST) {
            boxX -= DIST;
        }
        while (boxY <= yGoal - DIST) {
            boxY += DIST;
        }
        while (boxY >= yGoal + DIST) {
            boxY -= DIST;
        }

        MovingBox result = this.copy();
        result.move(boxX, boxY);
        return result;
    }

    public MovingBox copy() {
        return new MovingBox(x, y, xGoal, yGoal, width);
    }
}
