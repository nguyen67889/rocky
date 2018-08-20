package tester;
import problem.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class Tester {
    public static final double MAX_BASE_STEP = 0.001;
    public static final double MAX_ERROR = 0.0001;


    private ProblemSpec ps;
    private double angleError;

    public void Tester(ProblemSpec ps){
        this.ps = ps;
        angleError = Math.asin(MAX_ERROR/(ps.getRobotWidth()/2));
    }

    public Rectangle2D grow(Rectangle2D rect, double delta) {
        return new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,
                rect.getWidth() + 2 * delta, rect.getHeight() + 2 * delta);
    }

    public boolean TestInitialFirst(){
        System.out.println("Test Initial State");
        if (HasInitialFirst()) {
            System.out.println("Passed.");
            return true;
        } else {
            System.out.println("FAILED: Solution path must start at initial state.");
            return false;
        }
    }

    public boolean HasInitialFirst() {
        if (!ps.getInitialRobotConfig().equals(ps.getRobotPath().get(0))) {
            return false;
        }
        List<Box> movingBoxes = ps.getMovingBoxes();
        List<List<Box>> movingBoxesPath = ps.getMovingBoxPath();
        for (int i = 0; i < movingBoxes.size(); i++) {
            if (!movingBoxes.get(i).getPos().equals(movingBoxesPath.get(i).get(0).getPos())) {
                return false;
            }
        }

        List<Box> movingObstacles = ps.getMovingObstacles();
        List<List<Box>> movingObstaclePath = ps.getMovingObstaclePath();
        for (int i = 0; i < movingObstacles.size(); i++) {
            if (!movingObstacles.get(i).getPos().equals(movingObstaclePath.get(i).get(0).getPos())) {
                return false;
            }
        }

        return true;
    }

    public boolean testStepSize() {
        System.out.println("Test Step Size");
        List<RobotConfig> robotPath = ps.getRobotPath();
        RobotConfig last = robotPath.get(0);
        boolean tmp = true;

        for (int i = 1; i < robotPath.size(); i++) {
            if (!isValidStep(last, robotPath.get(i))) {
                System.out.println("Step size over 0.001 at step " + i);
                tmp = false;
            }
        }

        if (tmp) {
            System.out.println("Passed");
        }

        return tmp;
    }

    public boolean isValidStep(RobotConfig r1, RobotConfig r2) {
        if (r1.getPos().distance(r2.getPos()) > MAX_BASE_STEP) {
            return false;
        }
        if (getPoint2(r1).distance(getPoint2(r2)) > MAX_BASE_STEP) {
            return false;
        }
        return true;
    }

    public Point2D getPoint2(RobotConfig r) {
        double x = Math.cos(r.getOrientation()) * ps.getRobotWidth();
        double y = Math.sin(r.getOrientation()) * ps.getRobotWidth();
        return new Point2D.Double(x,y);
    }

    public int isCoupled(RobotConfig r, Box b) {
        Point2D p1,p2;
        double angle = (r.getOrientation() + Math.PI * 2) % (Math.PI * 2) + Math.PI * 2;
        boolean horizontal;
        if (angle >= Math.PI * 2 - angleError && angle <= Math.PI * 2 + angleError) {
            p1 = r.getPos();
            p2 = getPoint2(r);
            horizontal = true;
        } else if (angle >= Math.PI * 2.5 - angleError && angle <= Math.PI * 2.5 + angleError) {
            p1 = r.getPos();
            p2 = getPoint2(r);
            horizontal = false;
        } else if (angle >= Math.PI * 3 - angleError && angle <= Math.PI * 3 + angleError) {
            p2 = r.getPos();
            p1 = getPoint2(r);
            horizontal = true;
        } else if (angle >= Math.PI * 3.5 - angleError && angle <= Math.PI * 3.5 + angleError) {
            p2 = r.getPos();
            p1 = getPoint2(r);
            horizontal = false;
        } else {
            return -1;
        }

        return -1;
    }
}
