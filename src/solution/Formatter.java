package solution;

import java.util.ArrayList;
import java.util.List;

import solution.boxes.Movable;
import solution.states.State;

public class Formatter {

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
