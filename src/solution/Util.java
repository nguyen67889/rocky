package solution;

import java.math.BigDecimal;

public class Util {

    public enum Side {
        TOP, LEFT, BOTTOM, RIGHT
    }

    public static BigDecimal round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal number = new BigDecimal(value);
        return number.setScale(places, BigDecimal.ROUND_HALF_UP);
    }

    public static int round(double value) {
        BigDecimal number = new BigDecimal(value);
        return number.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    public static int roundHalf(double value) {
        BigDecimal number = new BigDecimal(value);
        return number.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }
}
