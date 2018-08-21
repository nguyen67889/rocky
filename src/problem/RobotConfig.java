package problem;

import java.awt.geom.Point2D;

public class RobotConfig {

    private Point2D pos;
    private double angle;

    public RobotConfig(double[] coords, double angle) {
        pos = new Point2D.Double(coords[0], coords[1]);
        this.angle = angle;
    }

    public RobotConfig(Point2D pos, double angle) {
        this.pos = (Point2D) pos.clone();
        this.angle = angle;
    }

    public Point2D getPos() {
        return pos;
    }

    public double getOrientation() {
        return angle;
    }

    public float getX1(double robotWidth) {
        return (float) (pos.getX() - Math.cos(angle) * robotWidth);
    }

    public float getX2(double robotWidth) {
        return (float) (pos.getX() + Math.cos(angle) * robotWidth);
    }

    public float getY1(double robotWidth) {
        return (float) (pos.getY() - Math.sin(angle) * robotWidth);
    }

    public float getY2(double robotWidth) {
        return (float) (pos.getY() + Math.sin(angle) * robotWidth);
    }

    public boolean equals(RobotConfig r2) {
        return (this.angle == r2.angle) && (this.pos.equals(r2.pos));
    }

}