package solution.krazysolution;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public abstract class Box {
    public static final int DIST = 100;

    int x;
    int y;
    int width;
    int height;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle2D getRect() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public Rectangle2D getExpandedRect() {
        //int expanded = (int)Math.ceil(width/Math.sqrt(2));
        int expanded = 400;
        return new Rectangle2D.Double(x - expanded, y - expanded, width + 2*expanded, height + 2*expanded);
    }

    public Rectangle2D getBottomEdge() {
        return new Rectangle2D.Double(x, y, width, height/2);
    }

    public Rectangle2D getTopEdge() {
        return new Rectangle2D.Double(x, y + height/2, width, height/2);
    }

    public Rectangle2D getLeftEdge() {
        return new Rectangle2D.Double(x, y, width/2, height);
    }

    public Rectangle2D getRightEdge() {
        return new Rectangle2D.Double(x + width/2, y, width/2, height);
    }

    public abstract Box copy();

    public boolean equals(Object o) {
        return o instanceof Box && ((Box) o).x == x && ((Box) o).y == y &&
                ((Box) o).width == width && ((Box) o).height == height;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        hash = 31 * hash + width;
        hash = 31 * hash + height;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("Box(x: %d, y: %d, w: %d, h: %d)", x, y, width, height);
    }

    public static class MBox extends Box {
        int xGoal;
        int yGoal;

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
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

        public MBox gridifyGoal() {
            int boxX = x;
            int boxY = y;
            while(boxX <= xGoal - DIST) {
                boxX += DIST;
            }
            while(boxX >= xGoal + DIST) {
                boxX -= DIST;
            }
            while(boxY <= yGoal - DIST) {
                boxY += DIST;
            }
            while(boxY >= yGoal + DIST) {
                boxY -= DIST;
            }
            MBox result = this.copy();
            result.setX(boxX);
            result.setY(boxY);
            return result;
        }

        public MBox(int x, int y, int xGoal, int yGoal, int width) {
            this.x = x;
            this.y = y;
            this.xGoal = xGoal;
            this.yGoal = yGoal;
            this.width = width;
            this.height = width;
        }

        public MBox copy() {
            return new MBox(x, y, xGoal, yGoal, width);
        }
    }

    public static class MObs extends MBox {
        public MObs(int x, int y, int width) {
            super(x, y, x, y, width);
        }

        public MObs copy() {
            return new MObs(x, y, width);
        }
    }

    public static class Obs extends Box {
        public Obs(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Obs copy() {
            return new Obs(x, y, width, height);
        }
    }
}
