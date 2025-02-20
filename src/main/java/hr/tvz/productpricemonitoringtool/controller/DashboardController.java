package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.UserCompanyRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the dashboard view.
 * Handles the company list and join code.
 * @see Company
 * @see User
 * @see CompanyRepository
 * @see UserCompanyRepository
 * @see AlertDialog
 * @see SceneLoader
 * @see Session
 */
public class DashboardController {

    @FXML public FlowPane companyFlowPane;
    @FXML public TextField joinCodeTextField;
    @FXML public GridPane addNewCompanyGridPane;

    private final CompanyRepository companyRepository = new CompanyRepository();
    private final UserCompanyRepository userCompanyRepository = new UserCompanyRepository();

    /**
     * Initializes the view.
     * Fills the list with the companies of the logged in user.
     * Redirects to the login view if no user is logged in.
     */
    public void initialize() {
        Session.setSelectedCompany(Optional.empty());

        Optional<User> user = Session.getLoggedInUser();
        if (user.isEmpty()) {
            SceneLoader.loadScene("login", "Login");
            return;
        }

        List<Company> companies = new ArrayList<>(user.get().getCompanies());
        companies.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));

        companyFlowPane.getChildren().clear();
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

        if (user.get().getRole().equals(Role.CUSTOMER)) {
            addNewCompanyGridPane.setVisible(false);
        }
    }

    /**
     * Handles the add new company button.
     * Redirects to the company add view.
     */
    public void handleAddNewCompany() {
        SceneLoader.loadScene("company_add", "Add Company");
    }

    /**
     * Handles the join company button.
     * Joins the company with the join code.
     */
    public void handleJoinCompany() {
        String joinCode = joinCodeTextField.getText();
        joinCodeTextField.clear();

        if (joinCode.isEmpty()) {
            return;
        }

        Optional<User> user = Session.getLoggedInUser();
        if (user.isEmpty()) {
            return;
        }

        Optional<Company> company;
        try {
            company = companyRepository.findAll().stream()
                    .filter(c -> c.getJoinCode().equals(joinCode))
                    .findFirst();
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while joining the company.");
            return;
        }

        if (company.isPresent()) {
            try {
                userCompanyRepository.addUser(user.get().getId(), company.get().getId());
                user.get().getCompanies().add(company.get());
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error while joining the company.");
                return;
            }

            initialize();
        } else {
            AlertDialog.showErrorDialog("Company with that join code doesn't exist.");
        }
    }
}
