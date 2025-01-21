package hr.tvz.productpricemonitoringtool.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuController {

    @FXML public ImageView userProfilePictureImageView;

    public void initialize() {
        userProfilePictureImageView.setImage(new Image("file:files/user/default_profile_picture.png"));
    }
}
