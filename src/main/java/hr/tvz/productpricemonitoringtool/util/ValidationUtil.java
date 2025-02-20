package hr.tvz.productpricemonitoringtool.util;

/**
 * ValidationUtil class.
 * Contains methods for validation.
 */
public class ValidationUtil {

    private ValidationUtil() {}

    /**
     * Method for checking if string is positive big decimal.
     */
    public static boolean isPositiveBigDecimal(String value) {
        return value.matches(Constants.POSITIVE_BIG_DECIMAL_REGEX);
    }

    /**
     * Method for checking if string is big decimal.
     */
    public static boolean isBigDecimal(String value) {
        return value.matches(Constants.BIG_DECIMAL_REGEX);
    }

    /**
     * Method for checking if string is positive integer.
     */
    public static boolean isPositiveInteger(String value) {
        return value.matches(Constants.POSITIVE_INTEGER_REGEX);
    }
}
