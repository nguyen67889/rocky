package tester;
import problem.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class Tester {
    public static final double MAX_BASE_STEP = 0.001;
    public static final double MAX_ERROR = 0.0001;


    ProblemSpec ps;


    public void Tester(ProblemSpec ps){
        this.ps = ps;
    }

    public Rectangle2D grow(Rectangle2D rect, double delta) {
        return new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,
                rect.getWidth() + 2 * delta, rect.getHeight() + 2 * delta);
    }

    public boolean TestInitialFirst(){
        System.out.println("Test Initial State");
        if (HasInitialFirst()) {
            System.out.println("Passed.");
            return true;
        } else {
            System.out.println("FAILED: Solution path must start at initial state.");
            return false;
        }
    }

    public boolean HasInitialFirst() {
        if (!ps.getInitialRobotConfig().equals(ps.getRobotPath().get(0))) {
            return false;
        }
        List<Box> movingBoxes = ps.getMovingBoxes();
        List<List<Point2D>> movingBoxesPath = ps.getMovingBoxPath();
        for (int i = 0; i < movingBoxes.size(); i++) {
            if (!movingBoxes.get(i).getPos().equals(movingBoxesPath.get(i).get(0))) {
                return false;
            }
        }

        List<Box> movingObstacles = ps.getMovingObstacles();
        List<List<Point2D>> movingObstaclePath = ps.getMovingObstaclePath();
        for (int i = 0; i < movingObstacles.size(); i++) {
            if (!movingObstacles.get(i).getPos().equals(movingObstaclePath.get(i).get(0))) {
                return false;
            }
        }

        return true;
    }
}
