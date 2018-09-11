package solution.krazysolution;

import java.math.BigDecimal;

public class Robot {
    public static final int DIST = 200;
    public static final BigDecimal ANGLE = BigDecimal.valueOf(45);

    private int x;
    private int y;
    private int width;
    private BigDecimal angle;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setAngle(BigDecimal angle) {
        this.angle = angle;
    }

    public void gridify() {
        if(x % DIST != 0) {
            x /= DIST;
        }
        if(y % DIST != 0) {
            y /= DIST;
        }
    }

    public int getX1() {
        return (int)(x + (width/2)*Math.cos(Math.toRadians(angle.doubleValue())));
    }

    public int getX2() {
        return (int)(x - (width/2)*Math.cos(Math.toRadians(angle.doubleValue())));
    }

    public int getY1() {
        return (int)(y + (width/2)*Math.sin(Math.toRadians(angle.doubleValue())));
    }

    public int getY2() {
        return (int)(y - (width/2)*Math.sin(Math.toRadians(angle.doubleValue())));
    }

    public int[] getFirstXs() {
        int[] xs = new int[4];
        xs[0] = getX1();
        xs[2] = getX();
        int x2 = getX2();
        xs[1] = (xs[0] + xs[2])/2;
        xs[3] = (xs[2] + x2)/2;
        return xs;
    }

    public int[] getLastXs() {
        int[] xs = new int[4];
        xs[3] = getX2();
        xs[1] = getX();
        int x1 = getX1();
        xs[2] = (xs[3] + xs[1])/2;
        xs[0] = (xs[1] + x1)/2;
        return xs;
    }

    public int[] getFirstYs() {
        int[] ys = new int[4];
        ys[0] = getY1();
        ys[2] = getY();
        int y2 = getY2();
        ys[1] = (ys[0] + ys[2])/2;
        ys[3] = (ys[2] + y2)/2;
        return ys;
    }

    public int[] getLastYs() {
        int[] ys = new int[4];
        ys[3] = getY2();
        ys[1] = getY();
        int y1 = getY1();
        ys[2] = (ys[3] + ys[1])/2;
        ys[0] = (ys[1] + y1)/2;
        return ys;
    }

    public int getWidth() {
        return width;
    }

    public BigDecimal getAngle() {
        return angle;
    }

    public void moveLeft() {
        x -= DIST;
    }

    public void moveRight() {
        x += DIST;
    }

    public void moveUp() {
        y += DIST;
    }

    public void moveDown() {
        y -= DIST;
    }

    public void rotateClockwise() {
        angle = angle.subtract(ANGLE);
        while(angle.doubleValue() >= 180) {
            angle = angle.subtract(BigDecimal.valueOf(180));
        }
        while(angle.doubleValue() < 0) {
            angle = angle.add(BigDecimal.valueOf(180));
        }
    }

    public void rotateAntiClockwise() {
        angle = angle.add(ANGLE);
        while(angle.doubleValue() >= 180) {
            angle = angle.subtract(BigDecimal.valueOf(180));
        }
        while(angle.doubleValue() < 0) {
            angle = angle.add(BigDecimal.valueOf(180));
        }
    }

    public Robot(int x, int y, int width, BigDecimal angle) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.angle = angle;
    }

    public Robot copy() {
        return new Robot(x, y, width, angle);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Robot && ((Robot) obj).x == x && ((Robot) obj).y == y &&
                ((Robot) obj).width == width && ((Robot) obj).angle.intValue() == angle.intValue();
    }

    @Override
    public int hashCode() {
        int hash = 11;
        hash = 43 * hash + x;
        hash = 43 * hash + y;
        hash = 43 * hash + width;
        hash = 43 * hash + (angle == null ? 0 : angle.intValue());

        return hash;
    }

    @Override
    public String toString() {
        return String.format("Robot(x: %d, y: %d, w: %d, a: %f)", x, y, width, angle.doubleValue());
    }
}
