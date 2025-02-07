package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController  {

    @FXML public TextField emailTextField;
    @FXML public PasswordField passwordPasswordField;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserRepository userRepository = new UserRepository();

    public void handleLogin() {
        String email = emailTextField.getText();
        String password = passwordPasswordField.getText();
        Optional<User> user;

        try {
            user = userRepository.findByEmailAndPassword(email, password);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error("Database connection is active. Please try again later.");
            return;
        }

        if (user.isEmpty()) {
            AlertDialog.showErrorDialog("Error", "Invalid email or password");
            logger.error("Invalid email or password");
            return;
        }

        Session.setLoggedInUser(user.get());
        logger.info("User with ID {} logged in", user.get().getId());
        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    public void handleRegisterOpen() {
        SceneLoader.loadScene("register", "Register");
    }
}
