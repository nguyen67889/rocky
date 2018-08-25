package solution;

import problem.ProblemSpec;

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



        System.out.println(inputFile);
        System.out.println(outputFile);
    }
}
