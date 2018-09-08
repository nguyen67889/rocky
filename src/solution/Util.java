package solution;

import java.math.BigDecimal;

public class Util {

    public enum Side {
        TOP, LEFT, BOTTOM, RIGHT
    }

    public static BigDecimal round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}
