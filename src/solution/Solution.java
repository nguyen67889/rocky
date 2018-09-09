package solution;

import java.awt.geom.Point2D.Double;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import problem.Box;
import problem.ProblemSpec;
import problem.RobotConfig;
import tester.Tester;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.List;

public class Solution {

    private ProblemSpec problem;

    private List<RobotConfig> robotPositions;
    private List<List<Point2D>> boxPositions;

    public Solution(ProblemSpec problem) {
        this.problem = problem;
    }

    public void saveSolution(String filename) throws IOException {
        BufferedWriter input = new BufferedWriter(new FileWriter(filename));

        if (robotPositions.size() != boxPositions.size()) {
            System.err.println("Saving Solution: Robot positions do not match box positions");
            System.exit(2);
        }

        int boxCount = boxPositions.get(0).size();
        RobotConfig robot;
        List<Point2D> boxes;

        input.write(robotPositions.size());
        for (int i = 0; i < robotPositions.size(); i++) {
            if (boxCount != boxPositions.get(i).size()) {
                System.err.println("Saving Solution: Box position #" + i + " does not match box count");
                System.exit(3);
            }

            robot = robotPositions.get(i);
            boxes = boxPositions.get(i);

            StringBuilder box = new StringBuilder();
            for (Point2D position : boxes) {
                box.append(" " + position.getX() + " " + position.getY());
            }

            input.write(robot.getPos().getX() + " " + robot.getPos().getY()
                    + " " + robot.getOrientation() + box.toString());

        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid Usage: java ProgramName inputFileName outputFileName");
            System.exit(1);
        }
        String inputFile = args[0];
        String outputFile = args[1];

        ProblemSpec problemSpec = new ProblemSpec();
        try {
            problemSpec.loadProblem(inputFile);
        } catch (IOException e) {
            System.err.println("FileIO Error: could not load input file");
        }

        Map<Box, List<Point2D>> movements = calculateMovements(problemSpec);

        try {
            BufferedWriter input = new BufferedWriter(new FileWriter(outputFile));
            input.write(Formatter.format(problemSpec, movements));
            input.flush();
        } catch (IOException e) {
            System.err.println("FileIO Error: could not output solution file");
        }

        try {
            problemSpec.loadSolution(outputFile);
        } catch (IOException e) {
            System.err.println("FileIO Error: could not read solution file");
        }

        test(problemSpec);
    }

    private static Map<Box, List<Point2D>> calculateMovements(ProblemSpec problem) {
        Grid<BigDecimal> grid = new Grid<>(problem);
        Node<BigDecimal>[][] map = grid.getGrid();

        Map<Box, List<Point2D>> movements = new HashMap<>();

        for (int i = 0; i < problem.getMovingBoxEndPositions().size(); i++) {
            Box box = problem.getMovingBoxes().get(i);
            Point2D goal = problem.getMovingBoxEndPositions().get(i);

            AStar<BigDecimal> aStar = new AStar<>(map, box.getPos(),
                    goal, BigDecimal.valueOf(box.getWidth()));
            List<Node<BigDecimal>> path = aStar.run();

            List<Point2D> coords = grid.getCoordPath(path, goal, box);

            Point2D lastPosition = coords.get(coords.size() - 1);
            grid.moveBox(box, lastPosition);

            movements.put(box, coords);
        }

        return movements;
    }

    private static void test(ProblemSpec problemSpec) {
        Tester tester = new Tester(problemSpec);

        if (problemSpec.getProblemLoaded() && problemSpec.getSolutionLoaded()) {
            System.out.println("Has initial state: " + tester.testInitialFirst());
            System.out.println("Correct step sizes: " + tester.testStepSize());
            System.out.println("Has no collisions: " + tester.testCollision());
            System.out.println("All pushes valid: " + tester.testPushedBox());
        } else if (problemSpec.getProblemLoaded()) {
            System.out.println("Problem has been loaded but no solution generated");
        } else {
            System.out.println("Problem has not been loaded correctly");
        }
    }
}
