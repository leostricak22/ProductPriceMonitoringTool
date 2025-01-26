package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Optional;


public class MenuController {

    @FXML public Circle userProfilePictureCircle;
    @FXML public Label companyNameTextLabel;
    @FXML public Label companyNameLabel;

    private Optional<Company> selectedCompany;

    public void initialize() {
        Image image = Session.getUserProfilePicture();
        userProfilePictureCircle.setFill(new ImagePattern(image));

        selectedCompany = Session.getSelectedCompany();
        if (selectedCompany.isPresent()) {
            companyNameLabel.setVisible(true);
            companyNameTextLabel.setVisible(true);
            companyNameLabel.setText(selectedCompany.get().getName());
        } else {
            companyNameLabel.setVisible(false);
            companyNameTextLabel.setVisible(false);
        }
    }

    public void handleDashboardRedirect() {
        if (selectedCompany.isPresent()) {
            SceneLoader.loadScene("company_dashboard", selectedCompany.get().getName());
            return;
        }

        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    public void handleUserEditRedirect() {
        SceneLoader.loadScene("user_edit", "Edit User");
    }
}
