package solution;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import problem.Box;
import problem.MovingBox;
import problem.ProblemSpec;
import problem.RobotConfig;

import tester.Tester;

public class Solution {

    private List<List<Point2D>> boxPositions = new ArrayList<>();
    private List<RobotConfig> robotPositions = new ArrayList<>();

    /**
     * Return a list of states containing a list of every boxes position.
     *
     * @return A list of lists of box positions.
     */
    public List<List<Point2D>> getBoxPositions() {
        return new ArrayList<>(boxPositions);
    }

    /**
     * Return a list of states containing a robot configuration.
     *
     * @return A list of robot configurations.
     */
    public List<RobotConfig> getRobotPositions() {
        return new ArrayList<>(robotPositions);
    }

    /**
     * Solve a problem based on the problem specification and update solution.
     *
     * @param problem The problem specification.
     */
    public void solve(ProblemSpec problem) {
        Map<Box, List<Point2D>> movements = Solution.calculateMovements(problem);

        Grid<BigDecimal> grid = new Grid<>(problem);
        RobotConfig robotConfig = problem.getInitialRobotConfig();
        List<Box> movable = new ArrayList<>(problem.getMovingBoxes());
        movable.addAll(problem.getMovingObstacles());

        for (Entry<Box, List<Point2D>> entry : movements.entrySet()) {
            Box currentBox = entry.getKey();

            Point2D goal = new Point2D.Double(
                    currentBox.getPos().getX(),
                    currentBox.getPos().getY() + currentBox.getWidth());

//            AStar<BigDecimal> aStar = new AStar<>(grid.getGrid(),
//                    robotConfig.getPos(), goal,
//                    BigDecimal.valueOf(problem.getRobotWidth()));
//            List<Node<BigDecimal>> path = aStar.run();
//            Box robotBox = new MovingBox(robotConfig.getPos(),
//                    problem.getRobotWidth());
//            List<Point2D> positions = grid.getCoordPath(path, goal, robotBox);
//
//            for (Point2D position : positions) {
//                robotConfig = new RobotConfig(
//                        new Double(position.getX(), position.getY()),
//                        robotConfig.getOrientation());
//
//                robotPositions.add(robotConfig);
//                boxPositions.add(generateBoxPositions(movable));
//            }

            for (Point2D point : entry.getValue()) {
                robotPositions.add(robotConfig);

                List<Point2D> boxes = new ArrayList<>();
                Point2D position;

                for (Box box : movable) {
                    double halfWidth = box.getWidth() / 2;

                    // Update box position if current box is moving box
                    if (box.equals(currentBox)) {
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

                boxPositions.add(boxes);
            }
        }
    }

    private List<Point2D> generateBoxPositions(List<Box> boxes) {
        List<Point2D> result = new ArrayList<>();
        Point2D position;
        for (Box box : boxes) {
            double halfWidth = box.getWidth() / 2;
            position = new Double(box.getPos().getX() + halfWidth,
                    box.getPos().getY() + halfWidth);
            result.add(position);
        }
        return result;
    }

    /**
     * Write a formatted solution to an output file.
     *
     * @param output The formatted solution.
     * @param outputFilename The filename to output the solution to.
     */
    private static void writeSolution(String output, String outputFilename) {
        try {
            BufferedWriter input = new BufferedWriter(
                    new FileWriter(outputFilename));
            input.write(output);
            input.flush();
        } catch (IOException e) {
            System.err.println("FileIO Error: could not output solution file");
        }
    }

    /**
     * Load a problem with no solution.
     *
     * @param problemFile Filename of the problem file.
     * @return A problem spec with no solution.
     */
    private static ProblemSpec loadProblem(String problemFile) {
        ProblemSpec problem = new ProblemSpec();
        try {
            problem.loadProblem(problemFile);
        } catch (IOException e) {
            System.err.println("FileIO Error: could not load input file");
        }
        return problem;
    }

    /**
     * Load a problem and solution into a problem spec.
     *
     * @param problemFile Filename of the problem file.
     * @param solutionFile Filename of the solution file.
     * @return A problem spec wih a solution.
     */
    private static ProblemSpec loadProblem(String problemFile, String solutionFile) {
        ProblemSpec problem = loadProblem(problemFile);
        try {
            problem.loadSolution(solutionFile);
        } catch (IOException e) {
            System.err.println("FileIO Error: could not read solution file");
        }
        return problem;
    }

    /**
     * Calcuate the movements required by all the boxes in a problem to reach
     * the solution using an A* algorithm.
     *
     * @param problem The problem specification.
     * @return A map of boxes to the list of positions they should move to.
     */
    private static Map<Box, List<Point2D>> calculateMovements(
            ProblemSpec problem) {
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

    /**
     * Use the tester on a specific problem to ensure correctness.
     *
     * @param problemSpec The problem specification with a solution.
     */
    private static void test(ProblemSpec problemSpec) {
        Tester tester = new Tester(problemSpec);

        if (problemSpec.getProblemLoaded() && problemSpec.getSolutionLoaded()) {
            System.out
                    .println("Has initial state: " + tester.testInitialFirst());
            System.out.println("Correct step sizes: " + tester.testStepSize());
            System.out.println("Has no collisions: " + tester.testCollision());
            System.out.println("All pushes valid: " + tester.testPushedBox());
        } else if (problemSpec.getProblemLoaded()) {
            System.out.println(
                    "Problem has been loaded but no solution generated");
        } else {
            System.out.println("Problem has not been loaded correctly");
        }
    }

    /**
     * Load a problem and generate a solution based on the arguments provided.
     *
     * Usage: java ProgramName inputFileName outputFileName
     *
     * @param args Command line args.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Invalid Usage: java ProgramName inputFileName outputFileName");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        ProblemSpec problemSpec = loadProblem(inputFile);
        Solution solution = new Solution();
        solution.solve(problemSpec);
        System.out.println(solution.getBoxPositions());

        String output = Formatter.format(solution);
        writeSolution(output, outputFile);

        problemSpec = loadProblem(inputFile, outputFile);
        test(problemSpec);
    }
}
