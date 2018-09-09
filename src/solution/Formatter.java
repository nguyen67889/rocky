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

    public static String format(ProblemSpec problem, Map<Box,
            List<Point2D>> movements, int count) {

        StringBuilder builder = new StringBuilder();
        builder.append(count).append("\n");

        for (Entry<Box, List<Point2D>> entry : movements.entrySet()) {
            for (Point2D point : entry.getValue()) {
                builder.append(
                        Formatter.formatRobot(problem.getInitialRobotConfig()));

                List<Point2D> boxes = new ArrayList<>();

                for (Box box : problem.getMovingBoxes().subList(0, 3)) {
                    Point2D.Double position;
                    if (box.equals(entry.getKey())) {
                        position = new Double(point.getX(), point.getY());
                    } else {
                        position = new Double(
                                box.getPos().getX() + box.getWidth() / 2,
                                box.getPos().getY() + box.getWidth() / 2);
                    }
                    boxes.add(position);
                }

                builder.append(Formatter.formatBoxPositions(boxes));
                builder.append("\n");
            }
        }

        return builder.toString();
    }

}
