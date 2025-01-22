package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.model.User;

import java.util.Optional;

public class Session {

    private static Optional<User> loggedInUser;

    private Session() {}

    public static void setLoggedInUser(User user) {
        loggedInUser = Optional.of(user);
    }

    public static Optional<User> getLoggedInUser() {
        return loggedInUser;
    }
}
