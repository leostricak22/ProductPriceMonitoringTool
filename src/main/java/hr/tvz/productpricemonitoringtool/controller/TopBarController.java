package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TopBarController {

    @FXML public Circle userProfilePictureCircle;
    @FXML public Rectangle notificationIconRectangle;

    public void initialize() {
        Image image = Session.getUserProfilePicture();
        userProfilePictureCircle.setFill(new ImagePattern(image));

        notificationIconRectangle.setFill(new ImagePattern(new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/notifications.png")));
    }

    public void handleUserEditRedirect() {
        SceneLoader.loadScene("user_edit", "Edit User");
    }

    public void handleFilter() {
        SceneLoader.loadPopupScene("map_radius", "Filter");
    }
}
