package solution.krazysolution;

import problem.MovingBox;
import problem.ProblemSpec;
import problem.RobotConfig;
import problem.StaticObstacle;
import solution.Util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.*;

public class State {
    private final static int AREA_SIZE = 10000; //area of the arena

    public List<Box.MBox> mBoxes;
    public List<Box.MObs> mObstacles;
    public List<Box.Obs> sObstacles;
    public Robot robot;

    public State(ProblemSpec spec) {
        RobotConfig specRobot = spec.getInitialRobotConfig();
        int robotX = Util.round(specRobot.getPos().getX() * AREA_SIZE, 0).intValue();
        int robotY = Util.round(specRobot.getPos().getY() * AREA_SIZE, 0).intValue();
        int robotW = Util.round(spec.getRobotWidth() * AREA_SIZE, 0).intValue();
        BigDecimal robotA = Util.round(Math.toDegrees(specRobot.getOrientation()), 4);
        this.robot = new Robot(robotX, robotY, robotW, robotA);

        List<StaticObstacle> obstacles = spec.getStaticObstacles();
        sObstacles = new ArrayList<>();
        for(StaticObstacle obs : obstacles) {
            int obsX = Util.round(obs.getRect().getX() * AREA_SIZE, 0).intValue();
            int obsY = Util.round(obs.getRect().getY() * AREA_SIZE, 0).intValue();
            int obsW = Util.round(obs.getRect().getWidth() * AREA_SIZE, 0).intValue();
            int obsH = Util.round(obs.getRect().getHeight() * AREA_SIZE, 0).intValue();
            sObstacles.add(new Box.Obs(obsX, obsY, obsW, obsH));
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
            mBoxes.add(new Box.MBox(boxX, boxY, boxGX, boxGY, boxW));
        }

        List<problem.Box> movingObstacles = spec.getMovingObstacles();
        mObstacles = new ArrayList<>();
        for(problem.Box box : movingObstacles) {
            int boxX = Util.round(box.getPos().getX() * AREA_SIZE, 0).intValue();
            int boxY = Util.round(box.getPos().getY() * AREA_SIZE, 0).intValue();
            int boxW = Util.round(box.getWidth() * AREA_SIZE, 0).intValue();
            mObstacles.add(new Box.MObs(boxX, boxY, boxW));
        }
    }

    public State(Robot robot, List<Box.MBox> mBoxes, List<Box.MObs> mObstacles, List<Box.Obs> sObstacles) {
        this.mBoxes = mBoxes;
        this.mObstacles = mObstacles;
        this.sObstacles = sObstacles;
        this.robot = robot;
    }

    public static String outputString(List<State> states) {
        StringBuilder sb = new StringBuilder();
        sb.append(states.size() + "\n");
        for(State state : states) {
            Robot robot = state.robot;
            sb.append((double)robot.getX()/AREA_SIZE + " ");
            sb.append((double)robot.getY()/AREA_SIZE + " ");
            sb.append(Util.round(Math.toRadians(robot.getAngle().doubleValue()), 4) + " ");
            for(Rectangle2D rect : state.getAllRects()) {
                sb.append((rect.getX() + rect.getWidth()/2)/AREA_SIZE + " ");
                sb.append((rect.getY() + rect.getHeight()/2)/AREA_SIZE + " ");
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
        for(int i = 0; i < current.mBoxes.size(); i++) {
            while(current.mBoxes.get(i).getX() < end.mBoxes.get(i).getX()) {
                current = current.saveState();
                current.mBoxes.get(i).setX(current.mBoxes.get(i).getX() + 10);
                states.add(current);
            }
            while(current.mBoxes.get(i).getX() > end.mBoxes.get(i).getX()) {
                current = current.saveState();
                current.mBoxes.get(i).setX(current.mBoxes.get(i).getX() - 10);
                states.add(current);
            }
            while(current.mBoxes.get(i).getY() < end.mBoxes.get(i).getY()) {
                current = current.saveState();
                current.mBoxes.get(i).setY(current.mBoxes.get(i).getY() + 10);
                states.add(current);
            }
            while(current.mBoxes.get(i).getY() > end.mBoxes.get(i).getY()) {
                current = current.saveState();
                current.mBoxes.get(i).setY(current.mBoxes.get(i).getY() - 10);
                states.add(current);
            }
        }
        //TODO: handle obstacles

        return states;
    }

    public List<Rectangle2D> getAllRects() {
        List<Rectangle2D> rects = new ArrayList<>();
        for(Box.MBox mBox : mBoxes) {
            rects.add(mBox.getRect());
        }
        for(Box.MObs mObs : mObstacles) {
            rects.add(mObs.getRect());
        }
        for(Box.Obs obs : sObstacles) {
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

    public boolean isBoxCollision(Box box) {
        if(box.getX() < 0 || box.getY() < 0 || box.getX() + box.getWidth() > AREA_SIZE ||
                box.getY() + box.getHeight() > AREA_SIZE) {
            return true;
        }

        for(Box.MBox mBox : mBoxes) {
            if(!box.equals(mBox) && mBox.getRect().intersects(box.getRect())) {
                return true;
            }
        }
        for(Box.MObs mObs : mObstacles) {
            if(!box.equals(mObs) && mObs.getRect().intersects(box.getRect())) {
                return true;
            }
        }
        for(Box.Obs obs : sObstacles) {
            if(!box.equals(obs) && obs.getRect().intersects(box.getRect())) {
                return true;
            }
        }

        return false;
    }

    public State saveState() {
        Robot robot = this.robot.copy();
        List<Box.MBox> mBoxes = new ArrayList<>();
        List<Box.MObs> mObstacles = new ArrayList<>();
        List<Box.Obs> sObstacles = new ArrayList<>();

        for(Box.MBox mBox : this.mBoxes) {
            mBoxes.add(mBox.copy());
        }
        for(Box.MObs mObs : this.mObstacles) {
            mObstacles.add(mObs.copy());
        }
        for(Box.Obs obs : this.sObstacles) {
            sObstacles.add(obs.copy());
        }

        return new State(robot, mBoxes, mObstacles, sObstacles);
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
        return mBoxes.get(0).toString();
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
}
