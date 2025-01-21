package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.model.User;

import java.util.Optional;

public class UserSession {

    private static Optional<User> loggedInUser;

    private UserSession() {}

    public static void setLoggedInUser(User user) {
        loggedInUser = Optional.of(user);
    }

    public static Optional<User> getLoggedInUser() {
        return loggedInUser;
    }
}
