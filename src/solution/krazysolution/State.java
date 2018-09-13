package solution.krazysolution;

import problem.ProblemSpec;
import problem.RobotConfig;
import solution.Util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.*;
import solution.boxes.Box;
import solution.boxes.MovingBox;
import solution.boxes.MovingObstacle;
import solution.boxes.StaticObstacle;

public class State {
    private final static int AREA_SIZE = 10000; //area of the arena
    private final static int CLOSE = 100;

    public List<MovingBox> mBoxes;
    public List<MovingObstacle> mObstacles;
    public List<StaticObstacle> sObstacles;
    public Robot robot;

    public Util.Side dir = null;
    public int current = 0;

    public State(ProblemSpec spec) {
        RobotConfig specRobot = spec.getInitialRobotConfig();
        int robotX = Util.round(specRobot.getPos().getX() * AREA_SIZE, 0).intValue();
        int robotY = Util.round(specRobot.getPos().getY() * AREA_SIZE, 0).intValue();
        int robotW = Util.round(spec.getRobotWidth() * AREA_SIZE, 0).intValue();
        BigDecimal robotA = Util.round(Math.toDegrees(specRobot.getOrientation()), 4);
        this.robot = new Robot(robotX, robotY, robotW, robotA);

        List<problem.StaticObstacle> obstacles = spec.getStaticObstacles();
        sObstacles = new ArrayList<>();
        for(problem.StaticObstacle obs : obstacles) {
            int obsX = Util.round(obs.getRect().getX() * AREA_SIZE, 0).intValue();
            int obsY = Util.round(obs.getRect().getY() * AREA_SIZE, 0).intValue();
            int obsW = Util.round(obs.getRect().getWidth() * AREA_SIZE, 0).intValue();
            int obsH = Util.round(obs.getRect().getHeight() * AREA_SIZE, 0).intValue();
            sObstacles.add(new StaticObstacle(obsX, obsY, obsW, obsH));
        }

        List<problem.Box> movingBoxes = spec.getMovingBoxes();
        mBoxes = new ArrayList<>();
        for(int i = 0; i < movingBoxes.size(); i++) {
            problem.Box box = movingBoxes.get(i);
            Point2D goal = spec.getMovingBoxEndPositions().get(i);
            int boxX = Util.round(box.getPos().getX() * AREA_SIZE, 0).intValue();
            int boxY = Util.round(box.getPos().getY() * AREA_SIZE, 0).intValue();
            int boxW = Util.round(box.getWidth() * AREA_SIZE, 0).intValue();
            int boxGX = Util.round(goal.getX() * AREA_SIZE, 0).intValue();
            int boxGY = Util.round(goal.getY() * AREA_SIZE, 0).intValue();
            mBoxes.add(new MovingBox(boxX, boxY, boxGX, boxGY, boxW));
        }

        List<problem.Box> movingObstacles = spec.getMovingObstacles();
        mObstacles = new ArrayList<>();
        for(problem.Box box : movingObstacles) {
            int boxX = Util.round(box.getPos().getX() * AREA_SIZE, 0).intValue();
            int boxY = Util.round(box.getPos().getY() * AREA_SIZE, 0).intValue();
            int boxW = Util.round(box.getWidth() * AREA_SIZE, 0).intValue();
            mObstacles.add(new MovingObstacle(boxX, boxY, boxW));
        }
    }

    public State(Robot robot, List<MovingBox> mBoxes, List<MovingObstacle> mObstacles, List<StaticObstacle> sObstacles) {
        this.mBoxes = mBoxes;
        this.mObstacles = mObstacles;
        this.sObstacles = sObstacles;
        this.robot = robot;
    }

    public static String outputString(List<State> states) {
        boolean above = true; //true: angle is in [0..180), false: angle is in [180..360)
        //I wish I didn't have to do it this way but the support code is so horrible I'm forced to
                StringBuilder sb = new StringBuilder();
        sb.append(states.size() + "\n");
        for(int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            if(i > 0 && Math.abs(states.get(i).robot.getAngle().doubleValue() -
                    states.get(i - 1).robot.getAngle().doubleValue()) > 1) { //0 <-> 180 snap
                above = !above;
            }

            Robot robot = state.robot;
            sb.append((double)robot.getX()/AREA_SIZE + " ");
            sb.append((double)robot.getY()/AREA_SIZE + " ");
            if(above) {
                sb.append(Util.round(Math.toRadians(robot.getAngle().doubleValue()), 4) + " ");
            } else {
                sb.append(Util.round(Math.toRadians(robot.getAngle().doubleValue() + 180), 4) + " ");
            }
            for(MovingBox mBox : state.mBoxes) {
                sb.append((mBox.getX() + (double)mBox.getWidth()/2)/AREA_SIZE + " ");
                sb.append((mBox.getY() + (double)mBox.getHeight()/2)/AREA_SIZE + " ");
            }
            for(MovingObstacle mObs : state.mObstacles) {
                sb.append((mObs.getX() + (double)mObs.getWidth()/2)/AREA_SIZE + " ");
                sb.append((mObs.getY() + (double)mObs.getHeight()/2)/AREA_SIZE + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /** If you're looking at this method and thinking "wtf is this"
     *
     * ...
     *       I'm thinking the exact same thing
     */
    public static List<State> interimStates(State start, State end) {
        List<State> states = new ArrayList<>();
        State current = start.saveState();
        states.add(current);

        while(current.robot.getX() < end.robot.getX()) {
            current = current.saveState();
            current.robot.setX(current.robot.getX() + 10);
            states.add(current);
        }
        while(current.robot.getX() > end.robot.getX()) {
            current = current.saveState();
            current.robot.setX(current.robot.getX() - 10);
            states.add(current);
        }
        while(current.robot.getY() < end.robot.getY()) {
            current = current.saveState();
            current.robot.setY(current.robot.getY() + 10);
            states.add(current);
        }
        while(current.robot.getY() > end.robot.getY()) {
            current = current.saveState();
            current.robot.setY(current.robot.getY() - 10);
            states.add(current);
        }
        if(current.robot.getAngle().intValue() >= 180) {
            current.robot.setAngle(current.robot.getAngle().subtract(BigDecimal.valueOf(180)));
        }
        if(current.robot.getAngle().intValue() == 0 && end.robot.getAngle().intValue() == 135) {
            current.robot.setAngle(BigDecimal.valueOf(180));
        }
        if(current.robot.getAngle().intValue() == 135 && end.robot.getAngle().intValue() == 0) {
            end.robot.setAngle(BigDecimal.valueOf(180));
        }
        while(current.robot.getAngle().doubleValue() < end.robot.getAngle().doubleValue()) {
            current = current.saveState();
            current.robot.setAngle(current.robot.getAngle().add(BigDecimal.valueOf(0.1)));
            states.add(current);
        }
        while(current.robot.getAngle().doubleValue() > end.robot.getAngle().doubleValue()) {
            current = current.saveState();
            current.robot.setAngle(current.robot.getAngle().subtract(BigDecimal.valueOf(0.1)));
            states.add(current);
        }
        if(current.robot.getAngle().intValue() >= 180) {
            current.robot.setAngle(current.robot.getAngle().subtract(BigDecimal.valueOf(180)));
        }

        return states;
    }

    public static List<State> interimBoxStates(State start, State end) {
        List<State> states = new ArrayList<>();
        State current = start.saveState();
        states.add(current);

        for (int i = 0; i < current.mBoxes.size(); i++) {
            while (current.mBoxes.get(i).getX() < end.mBoxes.get(i).getX()) {
                current = current.saveState();
                current.mBoxes.get(i).setX(current.mBoxes.get(i).getX() + 10);
                current.dir = Util.Side.LEFT;
                current.current = i + 1;

                states.add(current);
            }
            while (current.mBoxes.get(i).getX() > end.mBoxes.get(i).getX()) {
                current = current.saveState();
                current.mBoxes.get(i).setX(current.mBoxes.get(i).getX() - 10);
                current.dir = Util.Side.RIGHT;
                current.current = i + 1;

                states.add(current);
            }
            while (current.mBoxes.get(i).getY() < end.mBoxes.get(i).getY()) {
                current = current.saveState();
                current.mBoxes.get(i).setY(current.mBoxes.get(i).getY() + 10);
                current.dir = Util.Side.BOTTOM;
                current.current = i + 1;

                states.add(current);
            }
            while (current.mBoxes.get(i).getY() > end.mBoxes.get(i).getY()) {
                current = current.saveState();
                current.mBoxes.get(i).setY(current.mBoxes.get(i).getY() - 10);
                current.dir = Util.Side.TOP;
                current.current = i + 1;

                states.add(current);
            }
        }

        for (int i = 0; i < current.mObstacles.size(); i++) {
            while (current.mObstacles.get(i).getX() < end.mObstacles.get(i).getX()) {
                current = current.saveState();
                current.mObstacles.get(i).setX(current.mObstacles.get(i).getX() + 10);
                current.dir = Util.Side.LEFT;
                current.current = -i - 1;

                states.add(current);
            }
            while (current.mObstacles.get(i).getX() > end.mObstacles.get(i).getX()) {
                current = current.saveState();
                current.mObstacles.get(i).setX(current.mObstacles.get(i).getX() - 10);
                current.dir = Util.Side.RIGHT;
                current.current = -i - 1;

                states.add(current);
            }
            while (current.mObstacles.get(i).getY() < end.mObstacles.get(i).getY()) {
                current = current.saveState();
                current.mObstacles.get(i).setY(current.mObstacles.get(i).getY() + 10);
                current.dir = Util.Side.BOTTOM;
                current.current = -i - 1;

                states.add(current);
            }
            while (current.mObstacles.get(i).getY() > end.mObstacles.get(i).getY()) {
                current = current.saveState();
                current.mObstacles.get(i).setY(current.mObstacles.get(i).getY() - 10);
                current.dir = Util.Side.TOP;
                current.current = -i - 1;

                states.add(current);
            }
        }

        return states;
    }

    public List<Rectangle2D> getAllRects() {
        List<Rectangle2D> rects = new ArrayList<>();
        for(MovingBox mBox : mBoxes) {
            rects.add(mBox.getRect());
        }
        for(MovingObstacle mObs : mObstacles) {
            rects.add(mObs.getRect());
        }
        for(StaticObstacle obs : sObstacles) {
            rects.add(obs.getRect());
        }
        return rects;
    }

    public boolean isRobotCollision() {
        if(robot.getX1() < 0 || robot.getY1() < 0 || robot.getX2() < 0 || robot.getY2() < 0 ||
                robot.getX1() >= AREA_SIZE || robot.getX2() >= AREA_SIZE ||
                robot.getY1() >= AREA_SIZE || robot.getY2() >= AREA_SIZE) {
            return true;
        }

        Line2D line = new Line2D.Double(robot.getX1(), robot.getY1(), robot.getX2(), robot.getY2());

        for(Rectangle2D obs : getAllRects()) {
            if(obs.intersectsLine(line)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRobotOutOfBounds() {
        return robot.getX1() < 0 || robot.getY1() < 0 || robot.getX2() < 0 || robot.getY2() < 0 ||
                robot.getX1() >= AREA_SIZE || robot.getX2() >= AREA_SIZE ||
                robot.getY1() >= AREA_SIZE || robot.getY2() >= AREA_SIZE;
    }

    public MovingBox getRobotAlignment() {
        if(robot.getAngle().intValue() != 90 && robot.getAngle().intValue() != 0) {
            return null; //robot isn't aligned - why bother?
        }

        int[] firstXs = robot.getFirstXs();
        int[] firstYs = robot.getFirstYs();
        int[] lastXs = robot.getLastXs();
        int[] lastYs = robot.getLastYs();

        for(MovingBox mBox : mBoxes) {
            boolean alignedFirst = true;
            boolean alignedLast = true;
            for(int i = 0; i < 4; i++) {
                alignedFirst = alignedFirst && mBox.getRect().contains(new Point2D.Double(firstXs[i], firstYs[i]));
                alignedLast = alignedLast && mBox.getRect().contains(new Point2D.Double(lastXs[i], lastYs[i]));
            }
            if(alignedFirst || alignedLast) {
                return mBox;
            }
        }
        return null;

        //TODO: handle movable obstacles
    }

    public Util.Side getRobotAlignmentSide(Box box) {

        int[] firstXs = robot.getFirstXs();
        int[] firstYs = robot.getFirstYs();

        Point2D[] pts = {new Point2D.Double(firstXs[1], firstYs[1]),
            new Point2D.Double(firstXs[2], firstYs[2])};

        if(box.getBottomEdge().contains(pts[0]) && box.getBottomEdge().contains(pts[1])) {
            return Util.Side.BOTTOM;
        } else if(box.getLeftEdge().contains(pts[0]) && box.getLeftEdge().contains(pts[1])) {
            return Util.Side.LEFT;
        } else if(box.getRightEdge().contains(pts[0]) && box.getLeftEdge().contains(pts[1])) {
            return Util.Side.RIGHT;
        } else if(box.getTopEdge().contains(pts[0]) && box.getLeftEdge().contains(pts[1])) {
            return Util.Side.TOP;
        }
        return null;
    }

    public boolean isBoxCollision(Box box) {
        if(box.getX() < 200 || box.getY() < 200 || box.getX() + box.getWidth() > AREA_SIZE - 200 ||
                box.getY() + box.getHeight() > AREA_SIZE - 200) {
            return true;
        }

        for(MovingBox mBox : mBoxes) {
            if(!box.equals(mBox) && mBox.getRect().intersects(box.getExpandedRect())) {
                return true;
            }
        }
        for(MovingObstacle mObs : mObstacles) {
            if(!box.equals(mObs) && mObs.getRect().intersects(box.getExpandedRect())) {
                return true;
            }
        }
        for(StaticObstacle obs : sObstacles) {
            if(!box.equals(obs) && obs.getRect().intersects(box.getExpandedRect())) {
                return true;
            }
        }

        return false;
    }

    public State saveState() {
        Robot robot = this.robot.copy();
        List<MovingBox> mBoxes = new ArrayList<>();
        List<MovingObstacle> mObstacles = new ArrayList<>();
        List<StaticObstacle> sObstacles = new ArrayList<>();

        for(MovingBox mBox : this.mBoxes) {
            mBoxes.add(mBox.copy());
        }
        for(MovingObstacle mObs : this.mObstacles) {
            mObstacles.add(mObs.copy());
        }
        for(StaticObstacle obs : this.sObstacles) {
            sObstacles.add(obs.copy());
        }

        State newState = new State(robot, mBoxes, mObstacles, sObstacles);
        newState.dir = dir;
        newState.current = current;
        return newState;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 29 * hash + (mBoxes == null ? 0 : mBoxes.hashCode());
        hash = 29 * hash + (mObstacles == null ? 0 : mObstacles.hashCode());
        hash = 29 * hash + (sObstacles == null ? 0 : sObstacles.hashCode());
        hash = 29 * hash + (robot == null ? 0 : robot.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return robot.toString();
    }

    public boolean equals(Object o) {
        if(!(o instanceof State)) {
            return false;
        }
        State other = (State)o;
        return other.mBoxes.containsAll(mBoxes) && mBoxes.containsAll(other.mBoxes) &&
                other.mObstacles.containsAll(mObstacles) && mObstacles.containsAll(other.mObstacles) &&
                other.sObstacles.containsAll(sObstacles) && sObstacles.containsAll(other.sObstacles) &&
                other.robot.equals(robot);
    }

    public boolean isCloseBox(State state) {
        for(int i = 0; i < mBoxes.size(); i++) {
            if(Math.abs(mBoxes.get(i).getX() - state.mBoxes.get(i).getX()) > CLOSE ||
                    Math.abs(mBoxes.get(i).getY() - state.mBoxes.get(i).getY()) > CLOSE) {
                return false;
            }
        }
        return true;
    }

    public boolean isCloseObs(State state) {
        for(int i = 0; i < mObstacles.size(); i++) {
            if(Math.abs(mObstacles.get(i).getX() - state.mObstacles.get(i).getX()) > CLOSE ||
                    Math.abs(mObstacles.get(i).getY() - state.mObstacles.get(i).getY()) > CLOSE) {
                return false;
            }
        }
        return true;
    }

    public boolean isCloseRobot(State state) {
        return Math.abs(robot.getX() - state.robot.getX()) <= CLOSE &&
                Math.abs(robot.getY() - state.robot.getY()) <= CLOSE &&
                robot.getAngle().intValue() == state.robot.getAngle().intValue();
    }
}
