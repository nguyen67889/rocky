package solution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import problem.ProblemSpec;
import solution.states.State;
import tester.Tester;

/**
 * Automatically runs tests on the solution to determine statistics.
 */
public class AutoTester {
    // amount of times the solution has been run
    private int runs = 0;
    // amount of times the solution runs without errors
    private int successes = 0;
    // amount of time each run has taken to solve
    private List<Long> durations = new ArrayList<>();

    /**
     * Work out a solution to an input and test the solution.
     *
     * @param input The problem spec file.
     * @param output The output solution file.
     */
    private void testSolution(String input, String output) {
        successes++;

        // Load the problem & solution
        ProblemSpec problemSpec = Solution.loadProblem(input);
        Solution solution = new Solution(problemSpec);

        // Solve and time the solution
        long startTime = System.currentTimeMillis();
        List<State> states = solution.solve();
        long endTime = System.currentTimeMillis();
        durations.add(endTime - startTime);

        // Output the solution
        Solution.writeSolution(Formatter.format(states), output);

        problemSpec = Solution.loadProblem(input, output);
        Tester tester = new Tester(problemSpec);
        tester.testSolution();

        runs++;
    }

    /**
     * Executes the AutoTester
     */
    public static void main(String[] args) {
        AutoTester autoTester = new AutoTester();

        List<String> problems = new ArrayList<>();
        problems.add("case5.in");
        problems.add("case6.in");
        problems.add("case7.in");
        problems.add("caseTestMoveObstacle.in");

        File dir = new File("problems/tom");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (problems.contains(child.getName())) {
                    continue;
                }
                System.out.println("Testing " + child.getName());
                try {
                    autoTester.testSolution(child.getPath(),
                            "solutions/tom/" + child.getName() + ".out");
                } catch (Exception e) {
                    System.out.println("Failed: " + e.toString());
                }
                System.out.println();
            }
        }

        System.out.println("AutoTester Report\n");

        System.out.println("Durations");
        for (int i = 0; i < autoTester.durations.size(); i++) {
            System.out.println(i + ": " + (double) autoTester.durations.get(i) / 1000.0 + " seconds");
        }

        System.out.println();
        System.out.println("Successful Executions: " + autoTester.successes);
        System.out.println("Executions: " + autoTester.runs);
    }
}
