package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Optional;

/**
 * Controller for the company dashboard view.
 * Handles the company dashboard view.
 * @see Company
 * @see Session
 * @see SceneLoader
 */
public class CompanyDashboardController {

    @FXML
    public Label companyNameLabel;

    /**
     * Initializes the view.
     * Sets the company name label.
     */
    public void initialize() {
        Company selectedCompany;
        selectedCompany = Session.getSelectedCompany().orElseThrow(() -> new RuntimeException("No company selected."));
        companyNameLabel.setText(selectedCompany.getName());
    }

    /**
     * Handles the company logout.
     * Clears the selected company and redirects to the dashboard.
     */
    public void handleCompanyLogout() {
        Session.setSelectedCompany(Optional.empty());
        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    /**
     * Handles the product search redirect.
     * Redirects to the product search view.
     */
    public void handleAddProductRedirect() {
        SceneLoader.loadScene("product_add", "Add Product");
    }
}
