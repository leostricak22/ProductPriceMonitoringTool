package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.FileUtil;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CompanyProductsListController {

    @FXML
    public VBox itemListVbox;

    private final CompanyProductRepository companyProductRepository = new CompanyProductRepository();

    public void initialize() {
        if (Session.getSelectedCompany().isEmpty()) {
            AlertDialog.showErrorDialog("No company selected", "Please select a company first.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        Company selectedCompany = Session.getSelectedCompany().get();
        List<CompanyProduct> companyProducts = new ArrayList<>();
        try {
            companyProducts = new ArrayList<>(companyProductRepository.findByCompanyId(selectedCompany.getId()));
            companyProducts.sort(
                    (cp1, cp2) -> cp1.getProduct().getName().compareToIgnoreCase(cp2.getProduct().getName()));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Database connection active", "Please try again later.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        for (CompanyProduct companyProduct : companyProducts) {
            HBox productBox = new HBox();
            productBox.getStyleClass().add("product-box-container");
            productBox.setMaxHeight(100);
            productBox.setSpacing(15);

            Label idLabel = new Label(String.valueOf(companyProduct.getProduct().getId()));
            idLabel.setMaxHeight(100);
            productBox.getChildren().add(idLabel);

            ImageView imageView = new ImageView(FileUtil.cropImageToSquare(companyProduct.getProduct().getImage()));
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(90);

            HBox imageWrapper = new HBox(imageView);
            imageWrapper.setAlignment(Pos.CENTER);
            productBox.getChildren().add(imageWrapper);

            Label nameLabel = new Label(companyProduct.getProduct().getName());
            nameLabel.setMaxHeight(100);
            productBox.getChildren().add(nameLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            productBox.getChildren().add(spacer);

            Label priceLabel = new Label(String.valueOf(companyProduct.getPrice().value()));
            priceLabel.setMaxHeight(100);
            priceLabel.setStyle("-fx-font-weight: bold;");
            productBox.getChildren().add(priceLabel);

            productBox.setOnMouseClicked(event -> {
                Session.setSelectedProduct(companyProduct.getProduct());
                SceneLoader.loadScene("product-details", "Product details");
            });

            itemListVbox.getChildren().add(productBox);
        }
    }
}
