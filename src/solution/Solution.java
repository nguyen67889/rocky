package solution;

import problem.ProblemSpec;
import tester.Tester;

import java.io.IOException;

public class Solution {
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
