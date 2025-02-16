package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.util.Optional;

public class MenuController {

    @FXML public Label companyNameLabel;
    @FXML public MenuButton companyMenuButton;
    @FXML public MenuItem productsAddMenuItem;
    @FXML public MenuItem productsCompanyProductsMenuItem;

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
}
