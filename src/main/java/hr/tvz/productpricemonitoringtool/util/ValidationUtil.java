package hr.tvz.productpricemonitoringtool.util;

public class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isPositiveBigDecimal(String value) {
        return value.matches(Constants.POSITIVE_BIG_DECIMAL_REGEX);
    }

    public static boolean isBigDecimal(String value) {
        return value.matches(Constants.BIG_DECIMAL_REGEX);
    }
}
