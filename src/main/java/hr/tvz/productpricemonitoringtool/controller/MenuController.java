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

public class MenuController {

    @FXML public Label companyNameLabel;
    @FXML public MenuButton companyMenuButton;
    @FXML public MenuItem productsAddMenuItem;
    @FXML public MenuItem productsCompanyProductsMenuItem;
    @FXML public MenuButton adminMenuButton;

    private Optional<Company> selectedCompany;

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

    public void handleDashboardRedirect() {
        if (selectedCompany.isPresent()) {
            SceneLoader.loadScene("company_dashboard", selectedCompany.get().getName());
            return;
        }

        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    public void handleProductSearchRedirect() {
        SceneLoader.loadProductSearchScene(
                "product_search",
                "Product Search",
                Optional.empty());
    }

    public void handleProductAddRedirect() {
        SceneLoader.loadScene(
                "product_add",
                "Product Add");
    }

    public void handleCompanyProductsListRedirect() {
        SceneLoader.loadScene(
                "company_products_list",
                "Company Products List");
    }

    public void handleCompanyEditRedirect() {
        SceneLoader.loadScene(
                "company_edit",
                "Company Edit");
    }

    public void handleCompanyStaffRedirect() {
        SceneLoader.loadScene(
                "company_staff",
                "Company Staff");
    }

    public void handleAdminProductRedirect() {
        SceneLoader.loadScene(
                "admin_products",
                "Admin Products");
    }

    public void handleAdminCompanyRedirect() {
        SceneLoader.loadScene(
                "admin_companies",
                "Admin Companies");
    }

    public void handleAdminUserRedirect() {
        SceneLoader.loadScene(
                "admin_users",
                "Admin Users");
    }

    public void handleAdminCategoryRedirect() {
        SceneLoader.loadScene(
                "admin_categories",
                "Admin Categories");
    }

    public void handleAdminAddressRedirect() {
        SceneLoader.loadScene(
                "admin_addresses",
                "Admin Addresses");
    }

    public void handleAdminCompanyProductsRedirect() {
        SceneLoader.loadScene(
                "admin_company_products",
                "Admin Company Products");
    }

    public void handleAdminCompanyStaffRedirect() {
        SceneLoader.loadScene(
                "admin_company_staff",
                "Admin Company Staff");
    }

    public void handleAuditLogRedirect() {
        SceneLoader.loadScene(
                "audit_log",
                "Audit Log");
    }
}
