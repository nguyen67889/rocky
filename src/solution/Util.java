package solution;

import java.math.BigDecimal;

/**
 * Utility functions used throughout the project.
 */
public class Util {

    /**
     * The four possible sides of a box.
     */
    public enum Side {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    /**
     * Round a double to a given amount of decimal places.
     *
     * @param value The double value to round.
     * @param places The amount of places to round to.
     * @return The rounded value.
     */
    public static BigDecimal round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal number = new BigDecimal(value);
        return number.setScale(places, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Half round up a double value to an integer.
     *
     * @param value The double value to round.
     * @return The rounded double as an integer.
     */
    public static int round(double value) {
        BigDecimal number = new BigDecimal(value);
        return number.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    /**
     * Converts an angle to a radian to 4 decimal places.
     *
     * @param angle The angle in degrees to convert.
     * @return Radian value of the given angle.
     */
    public static BigDecimal toRadians(double angle) {
        return Util.round(Math.toRadians(angle), 4);
    }
}
