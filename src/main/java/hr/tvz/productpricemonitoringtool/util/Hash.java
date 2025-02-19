package hr.tvz.productpricemonitoringtool.util;

import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class Hash {

    private Hash() {}

    public static String generateJoinCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainPassword, salt);
    }
}
