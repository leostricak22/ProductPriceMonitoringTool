package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.model.User;
import javafx.scene.image.Image;

import java.io.File;
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

    public static Image getUserProfilePicture() {
        if (loggedInUser.isEmpty()) {
            throw new AuthenticationException("No user is logged in.");
        }

        for (String extension : Constants.IMAGE_EXTENSIONS) {
            String filePath = "files/user/" + loggedInUser.get().getId() + extension.substring(1);
            File userProfilePictureFile = new File(filePath);

            if (!userProfilePictureFile.exists())
                continue;

            return FileUtil.cropImageToSquare(new Image(userProfilePictureFile.toURI().toString()));
        }

        return FileUtil.cropImageToSquare(
                new Image("file:files/user/default_profile_picture.png"));
    }
}
