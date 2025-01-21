package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginController  {

    @FXML public TextField emailTextField;
    @FXML public PasswordField passwordPasswordField;

    UserRepository userRepository = new UserRepository();

    public void handleLogin() {
        String email = emailTextField.getText();
        String password = passwordPasswordField.getText();

        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if (user.isEmpty()) {
            AlertDialog.showErrorDialog("Error", "Invalid email or password");
            // TODO: Add logger
            return;
        }

        UserSession.setLoggedInUser(user.get());
        SceneLoader.loadScene("dashboard", "Product Price Monitoring Tool");
        // TODO: Add logger
    }
}
