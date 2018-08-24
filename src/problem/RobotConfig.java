package problem;

import java.awt.geom.Point2D;

/**
 * This class represents the configuration of the agent in Assignment 1.
 * 
 * @author Sergiy Dudnikov
 */
public class RobotConfig {

    /** The position of the robot */
    private Point2D pos;
    /** The orientation of the robot */
    private double angle;

    /**
	 * Constructs a configuration of the robot
	 * 
	 * @param coords
	 *            the position of the center of the robot in an array of 2 integers
	 * @param angle
	 *            the orientation of the robot
	 */
    public RobotConfig(double[] coords, double angle) {
        pos = new Point2D.Double(coords[0], coords[1]);
        this.angle = angle;
    }

    /**
	 * Constructs a configuration of the robot
	 * 
	 * @param coords
	 *            the position of the center of the robot as a Point2D
	 * @param angle
	 *            the orientation of the robot
	 */
    public RobotConfig(Point2D pos, double angle) {
        this.pos = (Point2D) pos.clone();
        this.angle = angle;
    }

    /** returns the position of the robot */
    public Point2D getPos() {
        return pos;
    }

    /** returns the orientation of the robot */
    public double getOrientation() {
        return angle;
    }

    /** check if 2 configurations are equal */
    public boolean equals(RobotConfig r2) {
        return (this.angle == r2.angle) && (this.pos.equals(r2.pos));
    

    @Override
    public boolean equals(Object o) {
        if (o instanceof  RobotConfig) {
            RobotConfig r = (RobotConfig) o;
            return (this.angle == r.angle) && (this.pos.equals(r.pos));
        }
        return false;
    }

}