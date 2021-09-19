package outsidergame.util;

public class MathUtil {
    public static double clampAng(double degrees) {
        degrees %= 360;
        if (degrees < 0) {
            degrees += 360;
        }

        return degrees;
    }

    public static double clampVals(double min, double max, double val) {
        if (val < min) {
            val = min;
        } else if (val > max) {
            val = max;
        }

        return val;
    }

    public static double getDistance(double mag1, double mag2) {
        return Math.sqrt(Math.pow(mag1, 2) + Math.pow(mag2, 2));
    }

    public static boolean isWithin(double minVal, double maxVal, double val) {
        if (clampVals(minVal, maxVal, val) != val) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isWithinCoordinateBounds(double objectCoord, double objectSize, double otherObjectCoord, double otherObjectSize) {
        return isWithin(objectCoord, objectCoord + objectSize, otherObjectCoord) || isWithin(objectCoord, objectCoord + objectSize, otherObjectCoord + otherObjectSize);
    }
}
