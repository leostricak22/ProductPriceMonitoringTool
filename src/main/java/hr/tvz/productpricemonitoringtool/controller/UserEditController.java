package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserFileRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the user edit view.
 * Handles the form for editing user information.
 * Validates the input and saves the user to the database.
 * @see User
 * @see UserFileRepository
 * @see AlertDialog
 * @see SceneLoader
 * @see Session
 * @see Constants
 * @see FileUtil
 */
public class UserEditController {

    @FXML public Circle userProfilePictureCircle;
    @FXML public TextField nameTextField;
    @FXML public TextField emailTextField;
    @FXML public TextField roleTextField;
    @FXML public Button applyChangesButton;
    @FXML public Label passwordChangeLabel;

    private User user;
    private Image image;
    private String imageUrl;
    boolean isImageChanged = false;

    private final UserFileRepository userFileRepository = new UserFileRepository();

    /**
     * Initializes the view.
     * Sets the user information and profile picture.
     */
    public void initialize() {
        user = Session.getLoggedInUser().orElseThrow(() -> new AuthenticationException("No user is logged in."));
        image = Session.getUserProfilePicture();

        imageUrl = image.getUrl();
        nameTextField.setText(user.getName());
        emailTextField.setText(user.getEmail());
        roleTextField.setText(user.getRole().toString());
        userProfilePictureCircle.setFill(new ImagePattern(image));

        if (user.getRole().equals(Role.CUSTOMER)) {
            nameTextField.setDisable(true);
            applyChangesButton.setDisable(true);
            passwordChangeLabel.setVisible(false);
        }
    }

    /**
     * Handles the profile picture pick button.
     * Picks a new profile picture for the user.
     */
    public void handlePickProfilePicture() {
        if (user.getRole().equals(Role.CUSTOMER))
            return;

        Optional<File> selectedFileOptional = FileUtil.pickFile(List.of("*.jpg", "*.jpeg", "*.png"));
        if (selectedFileOptional.isEmpty())
            return;

        imageUrl = selectedFileOptional.get().toURI().toString();
        image = FileUtil.cropImageToSquare(new Image(selectedFileOptional.get().toURI().toString()));
        userProfilePictureCircle.setFill(new ImagePattern(image));
        isImageChanged = true;
    }

    /**
     * Handles the edit user button.
     * Edits the user information and saves it to the database.
     */
    public void handleEdtUser() {
        user.setName(nameTextField.getText());

        userFileRepository.update(user);
        if (isImageChanged) {
            FileUtil.saveImage(imageUrl,
                    Constants.USER_PROFILE_PICTURES_PATH +
                            user.getId() + imageUrl.substring(imageUrl.lastIndexOf(".")));
        }

        AlertDialog.showInformationDialog("Success", "User edited successfully.");
        SceneLoader.loadScene("user_edit", "Edit User");
    }

    /**
     * Handles the edit password button.
     */
    public void handleUserEditPasswordRedirect() {
        SceneLoader.loadScene("user_edit_password", "Edit Password");
    }

    /**
     * Handles the logout button.
     * Logs out the user and redirects to the login view.
     */
    public void handleLogout() {
        Session.logout();
        SceneLoader.loadScene("login", "Login");
    }
}
