package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class MenuController {

    @FXML public Circle userProfilePictureCircle;

    public void initialize() {
        Image image = Session.getUserProfilePicture();
        userProfilePictureCircle.setFill(new ImagePattern(image));
    }

    public void handleDashboardRedirect() {
        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    public void handleUserEditRedirect() {
        SceneLoader.loadScene("user_edit", "Edit User");
    }
}
