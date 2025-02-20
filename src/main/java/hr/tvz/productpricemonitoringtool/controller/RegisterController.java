package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserFileRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * Controller for the register view.
 * Handles the registration form.
 * Validates the input and saves the user to the database.
 * @see User
 * @see UserFileRepository
 * @see AlertDialog
 * @see SceneLoader
 * @see Session
 */
public class RegisterController {

    @FXML public TextField nameTextField;
    @FXML public TextField surnameTextField;
    @FXML public TextField emailTextField;
    @FXML public PasswordField passwordField;
    @FXML public PasswordField confirmPasswordField;

    private final UserFileRepository userFileRepository = new UserFileRepository();

    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    /**
     * Handles the registration form.
     * Validates the input and saves the user to the database.
     */
    public void handleRegister() {
        String validationMessage = validateInput();

        if (!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            logger.error(validationMessage);
            return;
        }

        try {
            User user = userFileRepository.save(new User.Builder(0L, nameTextField.getText())
                    .surname(surnameTextField.getText())
                    .email(emailTextField.getText())
                    .password(passwordField.getText())
                    .role(Role.MERCHANT)
                    .companies(new HashSet<>())
                    .build());

            Session.setLoggedInUser(user);
            SceneLoader.loadScene("dashboard", "Dashboard");
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Failed to save user to file.");
        }
    }

    /**
     * Validates the input from the registration form.
     * @return validation message
     */
    private String validateInput() {
        String name = nameTextField.getText();
        String surname = surnameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "All fields are required!";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match!";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters long!";
        }

        if (!email.contains("@")) {
            return "Invalid email!";
        }

        if (userFileRepository.findByEmail(email).isPresent()) {
            return "User with this email already exists!";
        }

        return "";
    }

    /**
     * Handles the login button.
     */
    public void handleLoginOpen() {
        SceneLoader.loadScene("login", "Login");
    }
}
