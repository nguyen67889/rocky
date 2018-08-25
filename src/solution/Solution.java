package solution;

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

        test(problemSpec);
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
