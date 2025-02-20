package hr.tvz.productpricemonitoringtool.controller;

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

import java.util.Optional;

/**
 * Controller for the login view.
 * Handles the login form.
 * @see User
 * @see UserFileRepository
 * @see AlertDialog
 * @see SceneLoader
 * @see Session
 */
public class LoginController  {

    @FXML public TextField emailTextField;
    @FXML public PasswordField passwordPasswordField;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserFileRepository userFileRepository = new UserFileRepository();

    /**
     * Handles the login form.
     * Validates the input and logs the user in.
     */
    public void handleLogin() {
        String email = emailTextField.getText();
        String password = passwordPasswordField.getText();
        Optional<User> user = userFileRepository.findByEmailAndPassword(email, password);

        if (user.isEmpty()) {
            AlertDialog.showErrorDialog("Invalid email or password");
            logger.error("Invalid email or password");
            return;
        }

        Session.setLoggedInUser(user.get());
        logger.info("User with ID {} logged in", user.get().getId());
        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    /**
     * Handles the register button.
     * Redirects to the register view.
     */
    public void handleRegisterOpen() {
        SceneLoader.loadScene("register", "Register");
    }

    /**
     * Handles the continue as guest button.
     * Logs the user in as a guest.
     */
    public void handleContinueAsGuest() {
        Session.setLoggedInUser(Session.getGuestUser());
        SceneLoader.loadScene("dashboard", "Dashboard");
    }
}
