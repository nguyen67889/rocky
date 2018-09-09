package solution;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import problem.Box;
import problem.ProblemSpec;
import tester.Tester;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.List;

public class Solution {

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
        Map<Box, List<Point2D>> movements = calculateMovements(problemSpec);

        String output = Formatter.format(problemSpec, movements);
        writeSolution(output, outputFile);

        problemSpec = loadProblem(inputFile, outputFile);
        test(problemSpec);
    }
}
