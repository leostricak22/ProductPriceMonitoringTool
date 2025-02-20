package hr.tvz.productpricemonitoringtool.util;

import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

/**
 * Hash class.
 * Contains methods for generating join code and hashing password.
 */
public class Hash {

    private Hash() {}

    /**
     * Method for generating join code.
     * @return Join code.
     *         Generated join code.
     */
    public static String generateJoinCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    /**
     * Method for hashing password.
     * @param plainPassword Plain password.
     *                      Password to be hashed.
     * @return Hashed password.
     *         Hashed password.
     */
    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainPassword, salt);
    }
}
