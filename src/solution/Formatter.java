package solution;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import problem.RobotConfig;
import solution.boxes.Movable;
import solution.krazysolution.Robot;
import solution.krazysolution.State;

public class Formatter {

    /**
     * Format the output of a robots current configuration.
     *
     * @param robotConfig The robots configuration.
     * @return The formatted robot config.
     */
    public static StringBuilder formatRobot(RobotConfig robotConfig) {
        StringBuilder builder = new StringBuilder();

        builder.append(robotConfig.getPos().getX()).append(" ");
        builder.append(robotConfig.getPos().getY()).append(" ");
        builder.append(robotConfig.getOrientation());

        return builder;
    }

    /**
     * Format the output of a robots current configuration.
     *
     * @param robot The robots configuration.
     * @return The formatted robot config.
     */
    public static StringBuilder formatRobot(Robot robot, boolean above) {
        StringBuilder builder = new StringBuilder();

        int area = State.AREA_SIZE;

        builder.append((double)robot.getX()/area).append(" ");
        builder.append((double)robot.getY()/area).append(" ");

        double rotation = robot.getAngle().doubleValue();
        rotation = above ? rotation : rotation + 180;

        builder.append(Util.toRadians(rotation)).append(" ");

        return builder;
    }

    /**
     * Format the output of box positions.
     *
     * @param boxPositions A list of positions for boxes.
     * @return The formatted box positions.
     */
    public static StringBuilder formatBoxPositions(List<Point2D> boxPositions) {
        StringBuilder builder = new StringBuilder();

        for (Point2D position : boxPositions) {
            builder.append(" ").append(Util.round(position.getX(), 4));
            builder.append(" ").append(Util.round(position.getY(), 4));
        }

        return builder;
    }

    /**
     * Format the output of box positions.
     *
     * @param movables A list of positions for boxes.
     * @return The formatted box positions.
     */
    public static StringBuilder formatBoxes(List<Movable> movables) {
        StringBuilder builder = new StringBuilder();

        int area = State.AREA_SIZE;

        for(Movable movable : movables) {
            builder.append((movable.getX() + (double)movable.getWidth()/2)/area);
            builder.append(" ");
            builder.append((movable.getY() + (double)movable.getHeight()/2)/area);
            builder.append(" ");
        }

        return builder;
    }

    /**
     * Format output to a file for a solution to a problem.
     *
     * @param solution A solution instance that has been solved.
     * @return The formatted output.
     */
    public static String format(Solution solution) {
        List<List<Point2D>> boxes = solution.getBoxPositions();
        List<RobotConfig> robots = solution.getRobotPositions();

        if (boxes.size() != robots.size()) {
            throw new RuntimeException("Robot positions and box positions differ");
        }

        StringBuilder builder = new StringBuilder();
        builder.append(robots.size()).append("\n");

        for (int i = 0; i < robots.size(); i++) {
            builder.append(Formatter.formatRobot(robots.get(i)));
            builder.append(Formatter.formatBoxPositions(boxes.get(i)));
            builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * Format output to a file for a solution to a problem.
     *
     * @param states The series of states that makes up a solution.
     * @return The formatted output.
     */
    public static String format(List<State> states) {
        boolean above = true;

        //I wish I didn't have to do it this way but the support code is so horrible I'm forced to
        StringBuilder builder = new StringBuilder();
        builder.append(states.size()).append("\n");

        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            if(i > 0 && Math.abs(state.robot.getAngle().doubleValue() -
                    states.get(i - 1).robot.getAngle().doubleValue()) > 1) {
                above = !above;
            }

            // Format the robots position
            Robot robot = state.robot;
            builder.append(formatRobot(robot, above));

            // Format the box positions
            List<Movable> movables = new ArrayList<>(state.mBoxes);
            movables.addAll(state.mObstacles);
            builder.append(formatBoxes(movables));

            builder.append("\n");
        }

        return builder.toString();
    }

}
