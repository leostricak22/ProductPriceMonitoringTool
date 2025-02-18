package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.CryptoUtil;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class UserEditPasswordController {

    @FXML public PasswordField passwordField;
    @FXML public PasswordField confirmPasswordField;

    UserRepository userRepository = new UserRepository();

    public void handleEditPassword() {
        User user = Session.getLoggedInUser().orElseThrow(() -> new AuthenticationException("No user is logged in."));

        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String validationMessage = validateInput(password, confirmPassword);
        if (!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            return;
        }

        String hashedPassword = CryptoUtil.hash(password);
        user.setPassword(hashedPassword);

        userRepository.updatePassword(user);
        AlertDialog.showInformationDialog("Success", "Password successfully changed.");
        SceneLoader.loadScene("user_edit", "Edit user");
    }

    private String validateInput(String password, String confirmPassword) {
        if (password.trim().length() < 8) {
            return "Password must be at least 8 characters long.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        return "";
    }
}
