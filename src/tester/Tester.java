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

    public void testSolution() {
        boolean pass = true;
        if (ps.getProblemLoaded() && ps.getSolutionLoaded()) {
            pass = pass && testInitialFirst();
            pass = pass && testStepSize();
            pass = pass && testCollision();
            pass = pass && testPushedBox();
        }
        if (pass) {
            int count = countGoals();
            System.out.println(count + "out of " + ps.getMovingBoxes().size() + " goals reached");
        }
    }

    public int countGoals() {
        List<Box> finalState = ps.getMovingBoxPath().get(ps.getMovingBoxPath().size() - 1);
        int count = 0;
        for (int i = 0; i < finalState.size(); i++){
            if (finalState.get(i).getPos() == ps.getMovingBoxEndPositions().get(i)) {
                count++;
            }
        }
        return count;
    }

    public Rectangle2D grow(Rectangle2D rect, double delta) {
        return new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,
                rect.getWidth() + 2 * delta, rect.getHeight() + 2 * delta);
    }

    public boolean testInitialFirst(){
        System.out.println("Test Initial State");
        if (hasInitialFirst()) {
            System.out.println("Passed.");
            return true;
        } else {
            System.out.println("FAILED: Solution path must start at initial state.");
            return false;
        }
    }

    public boolean hasInitialFirst() {
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
        boolean pass = true;

        for (int i = 1; i < robotPath.size(); i++) {
            if (!isValidStep(last, robotPath.get(i))) {
                System.out.println("Step size over 0.001 at step " + i);
                pass = false;
            }
        }

        if (pass) {
            System.out.println("Passed");
        }

        return pass;
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
        double x = r.getPos().getX() + Math.cos(r.getOrientation()) * ps.getRobotWidth() * 0.5;
        double y = r.getPos().getY() + Math.sin(r.getOrientation()) * ps.getRobotWidth() * 0.5;
        return new Point2D.Double(x,y);
    }

    public Point2D getPoint1(RobotConfig r) {
        double x = r.getPos().getX() - Math.cos(r.getOrientation()) * ps.getRobotWidth() * 0.5;
        double y = r.getPos().getY() - Math.sin(r.getOrientation()) * ps.getRobotWidth() * 0.5;
        return new Point2D.Double(x,y);
    }

    public boolean testPushedBox() {
        System.out.println("Test pushed objects");
        boolean pass = true;
        for (int i = 1; i < ps.getRobotPath().size(); i++) {
            List<Box> oldMovingObjects = new ArrayList<Box>();
            List<Box> newMovingObjects = new ArrayList<Box>();
            oldMovingObjects.addAll(ps.getMovingBoxPath().get(i - 1));
            oldMovingObjects.addAll(ps.getMovingObstaclePath().get(i - 1));
            newMovingObjects.addAll(ps.getMovingBoxPath().get(i));
            newMovingObjects.addAll(ps.getMovingObstaclePath().get(i));
            int pushedBox = hasPushedBox(oldMovingObjects, newMovingObjects);

            switch (pushedBox){
                case -2: {
                    pass = false;
                    System.out.println("Multiple movable object moved at step" + i);
                }
                case -1 :continue;
                default: {
                    RobotConfig robot = ps.getRobotPath().get(i);
                    RobotConfig oldRobot = ps.getRobotPath().get(i - 1);
                    int direction = isCoupled(robot, newMovingObjects.get(pushedBox));
                    if (direction == -1) {
                        System.out.println("Robot not in pushing position but object moved at step " + i);
                        pass = false;
                    } else if (!testPushValidity(direction, oldRobot, robot, oldMovingObjects.get(pushedBox),
                            newMovingObjects.get(pushedBox))) {
                        System.out.println("Object not moving with robot" +
                                " or pushed to wrong direction at step " + i);
                        pass = false;
                    }
                }
            }
        }
        if (pass) {
            System.out.println("Passed");
        }
        return pass;
    }

    public boolean testPushValidity(int direction, RobotConfig oldrobot, RobotConfig newRobot, Box oldBox, Box newBox) {
        if (direction == -1) {
            return false;
        }

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

    public int hasPushedBox(List<Box> oldState, List<Box> currentState) {
        int pushedBox = -1;
        for(int i = 0; i < oldState.size(); i++) {
            if (!oldState.get(i).getPos().equals(currentState.get(i).getPos())) {
                if (pushedBox != -1) {
                    return -2;
                }
                pushedBox = i;
            }
        }
        return pushedBox;
    }

    public int isCoupled(RobotConfig r, Box b) {
        Point2D p1,p2;

        double angle = (r.getOrientation() + Math.PI * 2) % (Math.PI * 2) + Math.PI * 2;
        boolean horizontal;
        if (angle >= Math.PI * 2 - angleError && angle <= Math.PI * 2 + angleError) {
            p1 = getPoint1(r);
            p2 = getPoint2(r);
            horizontal = true;
        } else if (angle >= Math.PI * 2.5 - angleError && angle <= Math.PI * 2.5 + angleError) {
            p1 = getPoint1(r);
            p2 = getPoint2(r);
            horizontal = false;
        } else if (angle >= Math.PI * 3 - angleError && angle <= Math.PI * 3 + angleError) {
            p2 = getPoint1(r);
            p1 = getPoint2(r);
            horizontal = true;
        } else if (angle >= Math.PI * 3.5 - angleError && angle <= Math.PI * 3.5 + angleError) {
            p2 = getPoint1(r);
            p1 = getPoint2(r);
            horizontal = false;
        } else {
            return -1;
        }

        Rectangle2D collisionBox = grow(b.getRect(),MAX_ERROR);
        if ((!collisionBox.contains(p1)) && (!collisionBox.contains(p2))) {
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

    private boolean isCoincided(double m1, double m2, double n1, double n2) {
        if (m1 <= n1) {
            return (m2 - n1 >= 0.75 * ps.getRobotWidth());
        } else {
            return (n2 - m1 >= 0.85 * ps.getRobotWidth());
        }
    }

    public boolean testCollision(){
        System.out.println("Test collision:");
        boolean pass = true;
        for (int i = 0; i < ps.getRobotPath().size(); i++) {
            List<Box> movingObjects = new ArrayList<Box>();
            movingObjects.addAll(ps.getMovingBoxPath().get(i));
            movingObjects.addAll(ps.getMovingObstaclePath().get(i));
            if (!hasCollision(ps.getRobotPath().get(i), movingObjects)) {
                System.out.println("Collision at step + " + i);
                pass = false;
            }
        }
        if (pass) {
            System.out.println("Passed");
        }
        return pass;
    }

    public boolean hasCollision(RobotConfig r, List<Box> MovingObjects) {
        boolean coupled = false;
        Line2D robotLine = new Line2D.Double(getPoint1(r), getPoint2(r));
        Rectangle2D border = new Rectangle2D.Double(0,0,1,1);
        for (StaticObstacle o: ps.getStaticObstacles()) {
            if (robotLine.intersects(grow(o.getRect(), MAX_ERROR))) {
                return false;
            }
        }

        if (!border.contains(robotLine.getP1()) || !border.contains(robotLine.getP2())) {
            return false;
        }

        for (Box b1: MovingObjects) {

            if (!border.contains(b1.getRect())) {
                return false;
            }

            Rectangle2D collisionBox = grow(b1.getRect(),MAX_ERROR);
            if (collisionBox.intersectsLine(robotLine)) {
                if (coupled) {
                    return false;
                }
                if (isCoupled(r,b1) != -1) {
                    coupled = true;
                }
            }

            for (Box b2: MovingObjects) {
                if ((!b1.equals(b2)) && (collisionBox.intersects(b2.getRect()))) {
                    return false;
                }
            }

            for (StaticObstacle o: ps.getStaticObstacles()) {
                if (collisionBox.intersects(o.getRect()) || robotLine.intersects(o.getRect())) {
                    return false;
                }
            }
        }
        return true;
    }
}
