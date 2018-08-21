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
    private Box[] coupledBox;
    private int[] pushDirection;

    public void Tester(ProblemSpec ps){
        this.ps = ps;
        angleError = Math.asin(MAX_ERROR/(ps.getRobotWidth()/2));
        coupledBox = new Box[ps.getRobotPath().size()];
        pushDirection = new int[ps.getRobotPath().size()];
        for(int i = 0; i < ps.getRobotPath().size(); i++) {
            coupledBox[i] = null;
            pushDirection[i] = -1;
        }
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

    public boolean TestPushedBox() {
        System.out.println("Test pushed objects");
        List<List<Box>> movingBoxesPath = ps.getMovingBoxPath();
        List<List<Box>> movingObstaclesPath = ps.getMovingObstaclePath();
        boolean pass = true;
        for (int i = 1; i < movingBoxesPath.size(); i++) {
            List<Box> pushedBox = hasPushedBox(movingBoxesPath.get(i - 1), movingBoxesPath.get(i));
            List<Box> pushedObstacles = hasPushedBox(movingObstaclesPath.get(i - 1),movingObstaclesPath.get(i));
            int total = pushedBox.size() + pushedObstacles.size();
            if (total != 0) {
                if (total > 1) {
                    pass = false;
                    System.out.println("Multiple movable object moved at step" + i);
                } else {
                    if (pushedBox.size() == 1) {

                    }
                }
            }

        }
    }

    public boolean TestPushValidity(int direction, RobotConfig oldrobot, RobotConfig newRobot, Box oldBox, Box newBox) {
        double robotdy = newRobot.getPos().getY() - oldrobot.getPos().getY();
        double robotdx = newRobot.getPos().getX() - oldrobot.getPos().getX();
        double boxdy = newBox.getPos().getY() - oldBox.getPos().getY();
        double boxdx = newBox.getPos().getX() - oldBox.getPos().getX();
        if (robotdy - boxdy > MAX_ERROR || robotdx - boxdx > MAX_ERROR) {
            return false;
        }
        int actualDirection = 0;
        int moved = 0;

        if (boxdy > MAX_ERROR) {
            actualDirection = 1;
            moved++;
        } else if (boxdy < MAX_ERROR) {
            actualDirection = 3;
            moved++;
        } else if (boxdx > MAX_ERROR) {
            actualDirection = 2;
            moved++;
        } else if (boxdx < MAX_ERROR) {
            actualDirection = 4;
            moved++;
        }

        if (moved > 1) {
            return false;
        }

        if (actualDirection != 0 && actualDirection != direction) {
            return false;
        }
        return true;
    }

    public List<Box> hasPushedBox(List<Box> oldState, List<Box> currentState) {
        List<Box> pushedBox = new ArrayList<Box>();
        for(int i = 0; i < oldState.size(); i++) {
            if (!oldState.get(i).getPos().equals(currentState.get(i).getPos())) {
                pushedBox.add(currentState.get(i));
            }
        }
        return pushedBox;
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

        if (horizontal) {
            if (isCoincided(p1.getX(),p2.getX(),b.getRect().getMinX(),b.getRect().getMaxX())) {
                if (p1.getY() <= b.getPos().getY()) {
                    return 1;
                } else {return 3;}
            }
        } else if (isCoincided(p1.getY(),p2.getY(),b.getRect().getMinY(),b.getRect().getMaxY())) {
            if (p1.getX() <= b.getPos().getX()) {
                return 2;
            } else {return 4;}
        }

        return -1;
    }

    public boolean isCoincided(double m1, double m2, double n1, double n2) {
        if (m1 <= n1) {
            return (m2 - n1 >= 0.75 * ps.getRobotWidth());
        } else {
            return (n2 - m1 >= 0.85 * ps.getRobotWidth());
        }
    }
}
