package solution;

import java.util.ArrayList;
import java.util.List;
import problem.ProblemSpec;
import solution.states.State;
import tester.Tester;

/**
 * Automatically runs tests on the solution to determine statistics.
 */
public class AutoTester {
    // amount of collisions that have occurs
    private int collisions = 0;
    // amount of times the solution has been run
    private int runs = 0;
    // amount of time each run has taken to solve
    private List<Long> durations = new ArrayList<>();

    /**
     * Work out a solution to an input and test the solution.
     *
     * @param input The problem spec file.
     * @param output The output solution file.
     */
    private void testSolution(String input, String output) {
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

        if (!tester.testCollision()) {
            collisions++;
        }
        runs++;
    }

    /**
     * Executes the AutoTester
     */
    public static void main(String[] args) {
        AutoTester autoTester = new AutoTester();

        for (int i = 0; i < 20; i++) {
            autoTester.testSolution("problems/inputK.txt", "solutions/k" + i + ".txt");
        }

        System.out.println("AutoTester Report\n");

        System.out.println("Durations");
        for (int i = 0; i < autoTester.durations.size(); i++) {
            System.out.println(i + ": " + (double) autoTester.durations.get(i) / 1000.0 + " seconds");
        }

        System.out.println();
        System.out.println("Collisions: " + autoTester.collisions);
        System.out.println("Executions: " + autoTester.runs);

        double errorRate = (double) autoTester.collisions / (double) autoTester.runs;
        System.out.println("Failure Percent: " + errorRate * 100);
    }
}
