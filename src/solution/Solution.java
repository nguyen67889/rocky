package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import problem.ProblemSpec;

import solution.krazysolution.State;
import tester.Tester;

public class Solution {

    private ProblemSpec spec;

    public Solution(ProblemSpec spec) {
        this.spec = spec;
    }

    public List<State> solve() {
        return new ArrayList<>();
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
            System.exit(4);
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
            System.exit(2);
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
            System.exit(3);
        }
        return problem;
    }

    /**
     * Use the tester on a specific problem to ensure correctness.
     *
     * @param problemSpec The problem specification with a solution.
     */
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

    /**
     * Load a problem and generate a solution based on the arguments provided.
     *
     * Usage: java ProgramName inputFileName outputFileName
     *
     * @param args Command line args.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid Usage: java ProgramName inputFileName outputFileName");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        ProblemSpec problemSpec = loadProblem(inputFile);
        Solution solution = new Solution(problemSpec);
        List<State> states = solution.solve();

        writeSolution(Formatter.format(states), outputFile);

        problemSpec = loadProblem(inputFile, outputFile);
        test(problemSpec);
    }
}
