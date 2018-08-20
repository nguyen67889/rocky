package problem;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class represents the specifications of a given problem and solution;
 * that is, it provides a structured representation of the contents of a problem
 * text file and associated solution text file, as described in the assignment
 * specifications.
 * 
 * This class doesn't do any validity checking - see the code in tester.Tester
 * for this.
 * 
 * @author Sergiy Dudnikov
 */
public class ProblemSpec {
	/** True iff a problem is currently loaded */
	private boolean problemLoaded = false;
	/** True iff a solution is currently loaded */
	private boolean solutionLoaded = false;

    /** The static obstacles */
	private List<StaticObstacle> staticObstacles;

	// new vals
	private double robotWidth;

	/** The initial configuration */
	private RobotConfig initialRobotConfig;

    private List<Box> movingBoxes;
    private List<Box> movingObstacles;

    private int numMovingBoxes;
    private int numMovingObstacles;
    private int numStaticObstacles;

    private List<RobotConfig> robotPath = new ArrayList<>();
    private List<List<Point2D>> movingBoxPath = new ArrayList<>();
    private List<List<Point2D>> movingObstaclePath = new ArrayList<>();

    public double getRobotWidth() {return robotWidth;}

    public List<StaticObstacle> getStaticObstacles() {return staticObstacles;}

    public RobotConfig getInitialRobotConfig() { return initialRobotConfig; }

	public List<Box> getMovingBoxes() { return movingBoxes; }

	public List<Box> getMovingObstacles() { return movingObstacles; }

	public List<RobotConfig> getRobotPath() { return robotPath;}

	public List<List<Point2D>> getMovingBoxPath() { return movingBoxPath; }

	public List<List<Point2D>> getMovingObstaclePath() { return movingObstaclePath; }

    /**
	 * Loads a problem from a problem text file.
	 * 
	 * @param filename
	 *            the path of the text file to load.
	 * @throws IOException
	 *             if the text file doesn't exist or doesn't meet the assignment
	 *             specifications.
	 */
	public void loadProblem(String filename) throws IOException {
		problemLoaded = false;
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;
		int lineNo = 0;
		Scanner s;
		try {

			// line 1
			line = input.readLine();
			lineNo++;
			s = new Scanner(line);

			robotWidth = s.nextDouble();
			initialRobotConfig =  new RobotConfig(
				new Point2D.Double(s.nextDouble(), s.nextDouble()), s.nextDouble());
			s.close();

			// line 2
			line = input.readLine();
			lineNo++;
			s = new Scanner(line);
			// int numObstacles = s.nextInt();
			numMovingBoxes = s.nextInt();
			numMovingObstacles = s.nextInt();
			numStaticObstacles = s.nextInt();
			s.close();

			// this section covers moving boxes
			List<Point2D> movingBoxEndPositions = new ArrayList<Point2D>();
			movingBoxes = new ArrayList<>();
			for (int i = 0; i < numMovingBoxes; i++) {
				line = input.readLine();
				lineNo++;
				s = new Scanner(line);
				movingBoxes.add(new MovingBox(
					new Point2D.Double(s.nextDouble(), s.nextDouble()),	robotWidth));
				movingBoxEndPositions.add(
					new Point2D.Double(s.nextDouble(), s.nextDouble()));
				s.close();
			}

            movingObstacles = new ArrayList<>();

            // this section covers moving staticObstacles (still boxes)
			for (int i = 0; i < numMovingObstacles; i++) {
				line = input.readLine();
				lineNo++;
				s = new Scanner(line);
				movingObstacles.add(new MovingObstacle(
					new Point2D.Double(s.nextDouble(), s.nextDouble()),	s.nextDouble()));
				s.close();
			}
			
			// this section represents static staticObstacles
			staticObstacles = new ArrayList<StaticObstacle>();
			for (int i = 0; i < numStaticObstacles; i++) {
				line = input.readLine();
				lineNo++;
				staticObstacles.add(new StaticObstacle(line));
			}
			
			problemLoaded = true;
		} catch (InputMismatchException e) {
			System.out.format("Invalid number format on input file - line %d: %s", lineNo,
                    e.getMessage());
			System.exit(1);
		} catch (NoSuchElementException e) {
            System.out.format("Not enough tokens on input file - line %d",
                    lineNo);
            System.exit(2);
		} catch (NullPointerException e) {
            System.out.format("Input file - line %d expected, but file ended.", lineNo);
            System.exit(3);
		} finally {
			input.close();
		}
	}


    public void loadSolution(String filename) throws IOException {
        solutionLoaded = false;
        if (!problemLoaded) {
            System.out.println("Problem not loaded, exiting!");
            System.exit(4);
        }

        BufferedReader input = new BufferedReader(new FileReader(filename));
        String line;
        int lineNo = 0;
        Scanner s;
        try {
            // line 1
            line = input.readLine();
            lineNo++;
            s = new Scanner(line);
            int p = s.nextInt();
            s.close();

            // initial configuration


            for (int i = 0; i < p; i++) {
                line = input.readLine();
                lineNo++;
                s = new Scanner(line);
                robotPath.add(new RobotConfig(
                        new Point2D.Double(s.nextDouble(),s.nextDouble()),
                                s.nextDouble()));
                List<Point2D> movingBoxState = new ArrayList<>();
                for (int j = 0; j < numMovingBoxes; j++) {
                    movingBoxState.add(new Point2D.Double(s.nextDouble(),s.nextDouble()));
                }
                movingBoxPath.add(movingBoxState);
                List<Point2D> movingObstacleState = new ArrayList<>();
                for (int k = 0; k < numMovingObstacles; k++) {
                    movingObstacleState.add(new Point2D.Double(s.nextDouble(),s.nextDouble()));
                }
                movingObstaclePath.add(movingObstacleState);
                s.close();
            }
            solutionLoaded = true;
        } catch (InputMismatchException e) {
            System.out.format("Invalid number format on input file - line %d: %s", lineNo,
                    e.getMessage());
            System.exit(1);
        } catch (NoSuchElementException e) {
            System.out.format("Not enough tokens on input file - line %d",
                    lineNo);
            System.exit(2);
        } catch (NullPointerException e) {
            System.out.format("Input file - line %d expected, but file ended.", lineNo);
            System.exit(3);
        } finally {
            input.close();
        }
    }



}
