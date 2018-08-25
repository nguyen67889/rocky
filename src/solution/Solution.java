package solution;

public class Solution {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid Usage: java ProgramName inputFileName outputFileName");
            System.exit(1);
        }
        String inputFile = args[0];
        String outputFile = args[1];

        System.out.println(inputFile);
        System.out.println(outputFile);
    }
}
