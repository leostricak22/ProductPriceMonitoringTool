package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.util.Optional;
import java.util.Set;

public class DashboardController {

    @FXML
    public FlowPane companyFlowPane;

    public void initialize() {
        Session.setSelectedCompany(Optional.empty());

        Optional<User> user = Session.getLoggedInUser();
        if (user.isEmpty()) {
            SceneLoader.loadScene("login", "Login");
            return;
        }

        Set<Company> companies = user.get().getCompanies();

        for (Company company : companies) {
            Button companyButton = new Button(company.getName());
            companyButton.getStyleClass().add("company-button");
            companyButton.setOnAction(event -> {
                Session.setSelectedCompany(company);
                SceneLoader.loadScene("company_dashboard", company.getName());
            });
            companyFlowPane.getChildren().add(companyButton);
        }

        if (companies.isEmpty()) {
            Label noCompaniesLabel = new Label("You don't have any companies added yet.");
            companyFlowPane.getChildren().add(noCompaniesLabel);
        }
    }

    public void handleAddNewCompany() {
        SceneLoader.loadScene("company_add", "Add Company");
    }
}
