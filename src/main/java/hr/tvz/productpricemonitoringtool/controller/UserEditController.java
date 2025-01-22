package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class UserEditController {

    @FXML public Circle userProfilePictureCircle;
    @FXML public TextField nameTextField;
    @FXML public TextField emailTextField;
    @FXML public TextField roleTextField;

    private User user;
    private Image image;
    private String imageUrl;
    boolean isImageChanged = false;

    private final UserRepository userRepository = new UserRepository();

    public void initialize() {
        user = Session.getLoggedInUser().orElseThrow(() -> new AuthenticationException("No user is logged in."));
        image = Session.getUserProfilePicture();

        imageUrl = image.getUrl();
        nameTextField.setText(user.getName());
        emailTextField.setText(user.getEmail());
        roleTextField.setText(user.getRole().toString());
        userProfilePictureCircle.setFill(new ImagePattern(image));
    }

    public void handlePickProfilePicture() {
        Optional<File> selectedFileOptional = FileUtil.pickFile(List.of("*.jpg", "*.jpeg", "*.png"));
        if (selectedFileOptional.isEmpty())
            return;

        imageUrl = selectedFileOptional.get().toURI().toString();
        image = FileUtil.cropImageToSquare(new Image(selectedFileOptional.get().toURI().toString()));
        userProfilePictureCircle.setFill(new ImagePattern(image));
        isImageChanged = true;
    }

    public void handleEdtUser() {
        user.setName(nameTextField.getText());

        userRepository.update(user);
        if (isImageChanged) {
            FileUtil.saveImage(imageUrl,
                    Constants.USER_PROFILE_PICTURES_PATH +
                            user.getId() + imageUrl.substring(imageUrl.lastIndexOf(".")));
        }

        AlertDialog.showInformationDialog("Success", "User edited successfully.");
        SceneLoader.loadScene("user_edit", "Edit User");
    }

    public void handleUserEditPasswordRedirect() {
        SceneLoader.loadScene("user_edit_password", "Edit Password");
    }
}
