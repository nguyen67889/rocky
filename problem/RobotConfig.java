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


}