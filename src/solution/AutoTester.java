package solution;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import problem.ProblemSpec;
import solution.states.State;
import tester.Tester;
import visualiser.Visualiser;

/**
 * Automatically runs tests on the solution to determine statistics.
 */
public class AutoTester {
    // amount of times the solution has been run
    private int runs = 0;
    // amount of times the solution runs without errors
    private int successes = 0;
    // amount of times the solution passes all tests
    private int passes = 0;
    // amount of time each run has taken to solve
    private Map<String, Long> durations = new HashMap<>();

    // filenames that should be ignored when testing
    private List<String> excluded = new ArrayList<>();

    /**
     * Tests to see if a solution passes all the tests.
     *
     * @param solution The problem spec with a solution.
     * @return Whether all tests pass or not
     */
    public boolean solutionPasses(ProblemSpec solution) {
        Tester tester = new Tester(solution);

        boolean pass = tester.testInitialFirst();
        pass = tester.testStepSize() && pass;
        pass = tester.testCollision() && pass;
        pass = tester.testPushedBox() && pass;

        int goals = solution.getMovingBoxEndPositions().size();
        pass = tester.countGoals() == goals && pass;

        return pass;
    }

    /**
     * Work out a solution to an input and test the solution.
     *
     * @param input The problem spec file.
     * @param output The output solution file.
     */
    private void testSolution(String input, String output) {
        runs++;

        // Load the problem & solution
        ProblemSpec problemSpec = Solution.loadProblem(input);
        Solution solution = new Solution(problemSpec);

        // Solve and time the solution
        long startTime = System.currentTimeMillis();
        List<State> states = solution.solve();
        long endTime = System.currentTimeMillis();
        durations.put(input, endTime - startTime);

        // Output the solution
        Solution.writeSolution(Formatter.format(states), output);

        problemSpec = Solution.loadProblem(input, output);
        if (solutionPasses(problemSpec)) {
            passes++;
        }

        successes++;
    }

    /**
     * Register a list of files to be ignored by the tester.
     *
     * @param excludedFiles List of filenames to ignore.
     */
    public void registerExcluded(List<String> excludedFiles) {
        excluded.addAll(excludedFiles);
    }

    /**
     * Register a file to be ignored by the tester.
     *
     * @param excludedFile Filename to ignore.
     */
    public void registerExcluded(String excludedFile) {
        excluded.add(excludedFile);
    }

    /**
     * Test all the input files in a directory.
     *
     * @param problems A directory of input files.
     * @param solutions A directory to store the output files.
     */
    public void testDirectory(String problems, String solutions) {
        File dir = new File(problems);
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {

                if (child.isDirectory()) {
                    testDirectory(child.getPath(),
                            solutions + "/" + child.getName() + "/");
                    continue;
                }

                if (excluded.contains(child.getName())) {
                    continue;
                }

                System.out.println("Testing " + child.getName());
                String[] paths = child.getName().split("\\.");
                String outputFile = solutions + paths[0] + ".out";

                try {
                    testSolution(child.getPath(), outputFile);
//                    Visualiser.main(new String[]{child.getPath(), outputFile});
                } catch (Exception e) {
                    System.out.println("Failed: " + e.toString());
//                    Visualiser.main(new String[]{child.getPath()});
                }
                System.out.println();
            }
        }
    }

    /**
     * Executes the AutoTester
     */
    public static void main(String[] args) {
        AutoTester autoTester = new AutoTester();

        autoTester.registerExcluded("case5.in");
        autoTester.registerExcluded("case6.in");
        autoTester.registerExcluded("case7.in");
        autoTester.registerExcluded("caseTestMoveObstacle.in");
        autoTester.registerExcluded("caseMaxStaticObstaclesError.in");

        String rootDir = "tom";

        String problems = Paths.get("problems", rootDir).toString() + "/";
        String solutions = Paths.get("solutions", rootDir).toString() + "/";

        autoTester.testDirectory(problems, solutions);

        System.out.println("AutoTester Report\n");

        System.out.println("Durations");
        for (Entry<String, Long> duration : autoTester.durations.entrySet()) {
            System.out.println(duration.getKey() + ": " + (double) duration.getValue() / 1000.0 + " seconds");
        }

        System.out.println();
        System.out.println("No Error Runs: " + autoTester.successes);
        System.out.println("Tester Pass Runs: " + autoTester.passes);
        System.out.println("Executions: " + autoTester.runs);
    }
}