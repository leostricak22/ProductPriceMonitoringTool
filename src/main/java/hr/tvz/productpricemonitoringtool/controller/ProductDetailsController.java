package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class ProductDetailsController {

    @FXML public ImageView productImageView;
    @FXML public Label productNameLabel;
    @FXML public Label productPriceLabel;
    @FXML public Label productCategoryLabel;

    private final CompanyProductRepository companyProductRepository = new CompanyProductRepository();

    public void initialize() {
        if (Session.getSelectedProduct().isEmpty()) {
            AlertDialog.showErrorDialog("No product selected", "Please select a product first.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        Product selectedProduct = Session.getSelectedProduct().get();

        productImageView.setImage(selectedProduct.getImage());
        productNameLabel.setText(selectedProduct.getName());
        productCategoryLabel.setText(selectedProduct.getCategory().getName());

        Set<CompanyProduct> companyProducts = new HashSet<>();
        try {
            companyProducts = companyProductRepository.findByProductId(selectedProduct.getId());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Database connection active", "Please try again later.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        BigDecimal lowestPrice = companyProducts.stream()
                .map(companyProduct -> companyProduct.getPrice().value())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal highestPrice = companyProducts.stream()
                .map(companyProduct -> companyProduct.getPrice().value())
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        productPriceLabel.setText(lowestPrice.toString() + "€ - " + highestPrice.toString() + "€");
    }
}
