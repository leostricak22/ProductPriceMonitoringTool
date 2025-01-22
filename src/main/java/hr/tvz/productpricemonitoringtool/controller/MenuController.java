package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.Optional;

public class MenuController {

    @FXML public ImageView userProfilePictureImageView;

    public void initialize() {
        Optional<User> user = Session.getLoggedInUser();
        if (user.isEmpty()) {
            SceneLoader.loadScene("login", "Login");
            return;
        }

        Long userId = user.get().getId();
        String filePath = "files/user/" + userId + ".jpg";
        File userProfilePictureFile = new File(filePath);
        if (userProfilePictureFile.exists()) {
            userProfilePictureImageView.setImage(new Image(userProfilePictureFile.toURI().toString()));
        } else {
            userProfilePictureImageView.setImage(new Image("file:files/user/default_profile_picture.png"));
        }
    }

    public void handleDashboardRedirect() {
        SceneLoader.loadScene("dashboard", "Dashboard");
    }
}
