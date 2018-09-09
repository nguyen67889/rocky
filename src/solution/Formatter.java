package solution;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import problem.Box;
import problem.ProblemSpec;
import problem.RobotConfig;

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
     * Format output to a file for a problem spec and a mapping of box movements.
     *
     * @param problem The input problem specification.
     * @param movements A map of boxes to the list of positions they should move to.
     *
     * @return The formatted output.
     */
    public static String format(ProblemSpec problem, Map<Box, List<Point2D>> movements) {
        StringBuilder builder = new StringBuilder();

        int moves = 0;
        for (List<Point2D> positions : movements.values()) {
            moves += positions.size();
        }

        // Include a header of how many moves will be made
        builder.append(moves).append("\n");

        for (Entry<Box, List<Point2D>> entry : movements.entrySet()) {
            // For each move made by a box
            for (Point2D point : entry.getValue()) {
                // Output the robots location
                // TODO: Fix
                builder.append(
                        Formatter.formatRobot(problem.getInitialRobotConfig()));

                // List of all box positions at this state
                List<Point2D> boxes = new ArrayList<>();

                // List of all moveable objects in the map
                List<Box> movables = new ArrayList<>(problem.getMovingBoxes());
                movables.addAll(problem.getMovingObstacles());

                for (Box box : movables) {
                    Point2D.Double position;
                    double halfWidth = box.getWidth() / 2;

                    // Update box position if current box is moving box
                    if (box.equals(entry.getKey())) {
                        Point2D.Double newPos = new Double(
                                point.getX() - halfWidth,
                                point.getY() - halfWidth);
                        box.getPos().setLocation(newPos);
                    }

                    // Store the position for this box at this step
                    position = new Double(box.getPos().getX() + halfWidth,
                                box.getPos().getY() + halfWidth);
                    boxes.add(position);
                }

                builder.append(Formatter.formatBoxPositions(boxes));
                builder.append("\n");
            }
        }

        return builder.toString();
    }

}
