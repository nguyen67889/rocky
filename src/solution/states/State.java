package solution.states;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.*;

import problem.ProblemSpec;
import problem.RobotConfig;
import solution.Util;
import solution.boxes.Box;
import solution.boxes.MovingBox;
import solution.boxes.MovingObstacle;
import solution.boxes.StaticObstacle;
import solution.Robot;


public class State {
    // total area of the arena
    public final static int AREA_SIZE = 10000;
    // defines what it means for something to be "close" to something else
    public final static int CLOSE = 100;

    // the moving boxes in the state
    public List<MovingBox> mBoxes;
    // the moving obstacles in the state
    public List<MovingObstacle> mObstacles;
    // the static obstacles in the state
    public List<StaticObstacle> sObstacles;
    // the robot in the state
    public Robot robot;

    // the side the robot would need to move to in order to move an object
    public Util.Side dir = null;
    public int current = 0;

    /**
     * Creates a new State object
     * @param spec the problem specification to base the state on
     */
    public State(ProblemSpec spec) {
        // determines the robot's position and angle based on its configuration in the spec
        RobotConfig specRobot = spec.getInitialRobotConfig();
        int robotX = Util.round(specRobot.getPos().getX() * AREA_SIZE);
        int robotY = Util.round(specRobot.getPos().getY() * AREA_SIZE);
        int robotW = Util.round(spec.getRobotWidth() * AREA_SIZE);
        BigDecimal robotA = Util.round(Math.toDegrees(specRobot.getOrientation()), 4);
        this.robot = new Robot(robotX, robotY, robotW, robotA);

        // converts the Bad(tm) static obstacles to Good(tm) static obstacles
        List<problem.StaticObstacle> obstacles = spec.getStaticObstacles();
        sObstacles = new ArrayList<>();
        for(problem.StaticObstacle obs : obstacles) {
            sObstacles.add(StaticObstacle.convert(obs));
        }

        // converts the Bad(tm) moving boxes to Good(tm) moving boxes and stores
        // them with their goals
        List<problem.Box> movingBoxes = spec.getMovingBoxes();
        mBoxes = new ArrayList<>();
        for(int i = 0; i < movingBoxes.size(); i++) {
            problem.Box box = movingBoxes.get(i);
            Point2D goal = spec.getMovingBoxEndPositions().get(i);

            mBoxes.add(MovingBox.convert(box, goal));
        }

        // converts the Bad(tm) moving obstacles to Good(tm) moving obstacles
        List<problem.Box> movingObstacles = spec.getMovingObstacles();
        mObstacles = new ArrayList<>();
        for(problem.Box box : movingObstacles) {
            mObstacles.add(MovingObstacle.convert(box));
        }
    }

    /**
     * Creates a state based on existing Good(tm) versions of the various objects
     * in the state
     * @param robot the robot in the state
     * @param mBoxes the moving boxes in the state
     * @param mObstacles the moving obstacles in the state
     * @param sObstacles the static obstacles in the state
     */
    public State(Robot robot, List<MovingBox> mBoxes, List<MovingObstacle> mObstacles, List<StaticObstacle> sObstacles) {
        this.mBoxes = mBoxes;
        this.mObstacles = mObstacles;
        this.sObstacles = sObstacles;
        this.robot = robot;
    }

    /**
     * Creates a list of all of the possible states that could move the robot
     * towards the goal
     * @param start the state to start from
     * @param end the goal state
     * @return the list of states
     */
    public static List<State> interimStates(State start, State end) {
        List<State> states = new ArrayList<>();
        State current = start.saveState();
        states.add(current);

        // populates the list of states by exploring the ways to move the robot
        // closer to the goal state

        // robot is too far left
        while(current.robot.getX() < end.robot.getX()) {
            current = current.saveState();
            // moves robot right and saves the state
            current.robot.setX(current.robot.getX() + 10);
            states.add(current);
        }

        // robot is too far right
        while(current.robot.getX() > end.robot.getX()) {
            current = current.saveState();
            // moves robot left and saves the state
            current.robot.setX(current.robot.getX() - 10);
            states.add(current);
        }

        // robot is too far down
        while(current.robot.getY() < end.robot.getY()) {
            current = current.saveState();
            // moves robot up and saves the state
            current.robot.setY(current.robot.getY() + 10);
            states.add(current);
        }

        // robot is too far up
        while(current.robot.getY() > end.robot.getY()) {
            current = current.saveState();
            // moves the robot down and saves the state
            current.robot.setY(current.robot.getY() - 10);
            states.add(current);
        }

        // tries to align the current robot angle with something compatible with the end state
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

    /**
     * Creates a list of all the possible states that could move the boxes
     * towards their given goal (both moving boxes and movable obstacles)
     * @param start the starting state of the moving boxes
     * @param end the goal of the boxes
     * @return the list of states
     */
    public static List<State> interimBoxStates(State start, State end) {
        List<State> states = new ArrayList<>();
        State current = start.saveState();
        states.add(current);

        // looks at the moving boxes in the state
        for (int i = 0; i < current.mBoxes.size(); i++) {
            // checks for boxes that are too far left
            while (current.mBoxes.get(i).getX() < end.mBoxes.get(i).getX()) {
                current = current.saveState();
                // moves the box right, sets dir to be left, and saves the state
                current.mBoxes.get(i).setX(current.mBoxes.get(i).getX() + 10);
                current.dir = Util.Side.LEFT;
                current.current = i + 1;

                states.add(current);
            }

            // checks for boxes that are too far right
            while (current.mBoxes.get(i).getX() > end.mBoxes.get(i).getX()) {
                current = current.saveState();
                // moves the box left, sets dir to be right, and saves the state
                current.mBoxes.get(i).setX(current.mBoxes.get(i).getX() - 10);
                current.dir = Util.Side.RIGHT;
                current.current = i + 1;

                states.add(current);
            }

            // checks for boxes that are too far down
            while (current.mBoxes.get(i).getY() < end.mBoxes.get(i).getY()) {
                current = current.saveState();
                // moves the box up, sets dir to be bottom, and saves the state
                current.mBoxes.get(i).setY(current.mBoxes.get(i).getY() + 10);
                current.dir = Util.Side.BOTTOM;
                current.current = i + 1;

                states.add(current);
            }

            // checks for boxes that are too far up
            while (current.mBoxes.get(i).getY() > end.mBoxes.get(i).getY()) {
                current = current.saveState();
                // moves the box down, sets dir to be top, and saves the state
                current.mBoxes.get(i).setY(current.mBoxes.get(i).getY() - 10);
                current.dir = Util.Side.TOP;
                current.current = i + 1;

                states.add(current);
            }
        }

        // looks at the moving obstacles in the state
        for (int i = 0; i < current.mObstacles.size(); i++) {
            // checks for obstacles that are too far left
            while (current.mObstacles.get(i).getX() < end.mObstacles.get(i).getX()) {
                current = current.saveState();
                // moves the obstacle right, sets dir to be left, and saves the state
                current.mObstacles.get(i).setX(current.mObstacles.get(i).getX() + 10);
                current.dir = Util.Side.LEFT;
                current.current = -i - 1;

                states.add(current);
            }
            // checks for obstacles that are too far right
            while (current.mObstacles.get(i).getX() > end.mObstacles.get(i).getX()) {
                current = current.saveState();
                // moves the obstacle left, sets dir to be right, and saves the state
                current.mObstacles.get(i).setX(current.mObstacles.get(i).getX() - 10);
                current.dir = Util.Side.RIGHT;
                current.current = -i - 1;

                states.add(current);
            }
            // checks for obstacles that are too far down
            while (current.mObstacles.get(i).getY() < end.mObstacles.get(i).getY()) {
                current = current.saveState();
                // moves the obstacle up, sets dir to be bottom, and saves the state
                current.mObstacles.get(i).setY(current.mObstacles.get(i).getY() + 10);
                current.dir = Util.Side.BOTTOM;
                current.current = -i - 1;

                states.add(current);
            }
            // checks for obstacles that are too far up
            while (current.mObstacles.get(i).getY() > end.mObstacles.get(i).getY()) {
                current = current.saveState();
                // moves the obstacle up, sets dir to be top, and saves the state
                current.mObstacles.get(i).setY(current.mObstacles.get(i).getY() - 10);
                current.dir = Util.Side.TOP;
                current.current = -i - 1;

                states.add(current);
            }
        }

        return states;
    }

    /**
     * Gets a list of all moving boxes, moving obstacles, and static obstacles
     * @return the list of all the boxes
     */
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

    /**
     * Checks whether the robot is colliding with anything in the state (or is
     * out of bounds)
     * @return true if there is a collision, false otherwise
     */
    public boolean isRobotCollision() {
        // checks if the robot has gone out of bounds
        if (robot.getX1() < 0 || robot.getY1() < 0 || robot.getX2() < 0 || robot.getY2() < 0 ||
                robot.getX1() >= AREA_SIZE || robot.getX2() >= AREA_SIZE ||
                robot.getY1() >= AREA_SIZE || robot.getY2() >= AREA_SIZE) {
            return true;
        }

        // creates a Line2D representation of the robot
        Line2D line = new Line2D.Double(robot.getX1(), robot.getY1(), robot.getX2(), robot.getY2());

        // checks for collisions with all the rectangles in the state
        for (Rectangle2D obs : getAllRects()) {
            if (obs.intersectsLine(line)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given box is colliding with anything else in the state
     * @param box the box to check
     * @return true if there is a collision, false otherwise
     */
    public boolean isBoxCollision(Box box) {
        if (box.getX() < 200 || box.getY() < 200 || box.getX() + box.getWidth() > AREA_SIZE - 200 ||
                box.getY() + box.getHeight() > AREA_SIZE - 200) {
            return true;
        }

        for (MovingBox mBox : mBoxes) {
            if (!box.equals(mBox) && mBox.getRect().intersects(box.getExpandedRect())) {
                return true;
            }
        }
        for (MovingObstacle mObs : mObstacles) {
            if (!box.equals(mObs) && mObs.getRect().intersects(box.getExpandedRect())) {
                return true;
            }
        }
        for (StaticObstacle obs : sObstacles) {
            if (!box.equals(obs) && obs.getRect().intersects(box.getExpandedRect())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a copy of the current state, with deep copies of all of its
     * components
     * @return the new copied state
     */
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

    /**
     * Checks if any of the boxes in the current state are close to those in the
     * given state
     * @param state the state to check
     * @return true if there is a close box, false otherwise
     */
    public boolean isCloseBox(State state) {
        for (int i = 0; i < mBoxes.size(); i++) {
            if (Math.abs(mBoxes.get(i).getX() - state.mBoxes.get(i).getX()) > CLOSE ||
                    Math.abs(mBoxes.get(i).getY() - state.mBoxes.get(i).getY()) > CLOSE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if any of the obstacles in the current state are close to those in
     * the given state
     * @param state the obstacle to check
     * @return true if there is a close obstacle, false otherwise
     */
    public boolean isCloseObs(State state) {
        for (int i = 0; i < mObstacles.size(); i++) {
            if (Math.abs(mObstacles.get(i).getX() - state.mObstacles.get(i).getX()) > CLOSE ||
                    Math.abs(mObstacles.get(i).getY() - state.mObstacles.get(i).getY()) > CLOSE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the robot in the current state is close to that in the given state
     * @param state the state to check against
     * @return true if the robot is close, false otherwise
     */
    public boolean isCloseRobot(State state) {
        return Math.abs(robot.getX() - state.robot.getX()) <= CLOSE &&
                Math.abs(robot.getY() - state.robot.getY()) <= CLOSE &&
                robot.getAngle().intValue() == state.robot.getAngle().intValue();
    }
}
