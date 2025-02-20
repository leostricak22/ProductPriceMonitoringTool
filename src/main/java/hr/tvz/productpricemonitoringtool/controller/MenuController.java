package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.util.Objects;
import java.util.Optional;

/**
 * Controller for the menu view.
 * Handles the menu items and redirects.
 * @see Company
 * @see SceneLoader
 * @see Session
 */
public class MenuController {

    @FXML public Label companyNameLabel;
    @FXML public MenuButton companyMenuButton;
    @FXML public MenuItem productsAddMenuItem;
    @FXML public MenuItem productsCompanyProductsMenuItem;
    @FXML public MenuButton adminMenuButton;

    private Optional<Company> selectedCompany;

    /**
     * Initializes the view.
     * Sets the company name label if present.
     */
    public void initialize() {
        selectedCompany = Session.getSelectedCompany();
        if (selectedCompany.isPresent()) {
            companyNameLabel.setVisible(true);
            companyNameLabel.setText("Currently in: " + selectedCompany.get().getName());
        } else {
            companyNameLabel.setVisible(false);

            companyMenuButton.setDisable(true);
            productsAddMenuItem.setDisable(true);
            productsCompanyProductsMenuItem.setDisable(true);
        }

        companyMenuButton.setVisible(!Objects.equals(Session.getLoggedInUser().get().getRole(), Role.CUSTOMER));
        adminMenuButton.setVisible(Session.getLoggedInUser().get().getRole().equals(Role.ADMIN));
    }

    /**
     * Handles the dashboard redirect.
     * Redirects to the dashboard view.
     */
    public void handleDashboardRedirect() {
        if (selectedCompany.isPresent()) {
            SceneLoader.loadScene("company_dashboard", selectedCompany.get().getName());
            return;
        }

        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    /**
     * Handles open product search screen.
     */
    public void handleProductSearchRedirect() {
        SceneLoader.loadProductSearchScene(
                "product_search",
                "Product Search",
                Optional.empty());
    }

    /**
     * Handles open product add screen.
     */
    public void handleProductAddRedirect() {
        SceneLoader.loadScene(
                "product_add",
                "Product Add");
    }

    /**
     * Handles the company products list redirect.
     */
    public void handleCompanyProductsListRedirect() {
        SceneLoader.loadScene(
                "company_products_list",
                "Company Products List");
    }

    /**
     * Handles the company edit redirect.
     */
    public void handleCompanyEditRedirect() {
        SceneLoader.loadScene(
                "company_edit",
                "Company Edit");
    }

    /**
     * Handles open company staff screen.
     */
    public void handleCompanyStaffRedirect() {
        SceneLoader.loadScene(
                "company_staff",
                "Company Staff");
    }

    /**
     * handles open all products screen.
     */
    public void handleAdminProductRedirect() {
        SceneLoader.loadScene(
                "admin_products",
                "Admin Products");
    }

    /**
     * Handles open all companies screen.
     */
    public void handleAdminCompanyRedirect() {
        SceneLoader.loadScene(
                "admin_companies",
                "Admin Companies");
    }

    /**
     * Handles open all users screen.
     */
    public void handleAdminUserRedirect() {
        SceneLoader.loadScene(
                "admin_users",
                "Admin Users");
    }

    /**
     * Handles open all categories screen.
     */
    public void handleAdminCategoryRedirect() {
        SceneLoader.loadScene(
                "admin_categories",
                "Admin Categories");
    }

    /**
     * Handles open all addresses screen.
     */
    public void handleAdminAddressRedirect() {
        SceneLoader.loadScene(
                "admin_addresses",
                "Admin Addresses");
    }

    /**
     * Handles open all company products screen.
     */
    public void handleAdminCompanyProductsRedirect() {
        SceneLoader.loadScene(
                "admin_company_products",
                "Admin Company Products");
    }

    /**
     * Handles open all company staff screen.
     */
    public void handleAdminCompanyStaffRedirect() {
        SceneLoader.loadScene(
                "admin_company_staff",
                "Admin Company Staff");
    }

    /**
     * Handles open all audit logs screen.
     */
    public void handleAuditLogRedirect() {
        SceneLoader.loadScene(
                "audit_log",
                "Audit Log");
    }
}
