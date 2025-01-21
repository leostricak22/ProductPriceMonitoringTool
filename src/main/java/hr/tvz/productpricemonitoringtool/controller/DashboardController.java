package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML public Label welcomeLabel;

    public void initialize() {
        UserSession.getLoggedInUser().ifPresent(user -> welcomeLabel.setText("Welcome: " + user.getName()));
    }
}
