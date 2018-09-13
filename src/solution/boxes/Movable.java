package solution.boxes;

public abstract class Movable extends Box {

    public Movable(int x, int y, int width) {
        super(x, y, width);
    }

    public Movable(int x, int y, int width, int height) {
        super(x, y, width, height);
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

}
