package tester;
import problem.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class Tester {
    public static final double MAX_BASE_STEP = 0.001;
    public static final double MAX_ERROR = 0.0001;
    ProblemSpec ps;


    public void Tester(ProblemSpec ps){
        this.ps = ps;
    }

    public static Rectangle2D grow(Rectangle2D rect, double delta) {
        return new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,
                rect.getWidth() + 2 * delta, rect.getHeight() + 2 * delta);
    }

    public static boolean TestInitial(){
        return false;
    }
}
