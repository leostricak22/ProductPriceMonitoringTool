package hr.tvz.productpricemonitoringtool.util;

import java.util.UUID;

public class Hash {

    private Hash() {}

    public static String generateJoinCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
