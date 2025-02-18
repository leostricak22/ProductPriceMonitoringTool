package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Objects.isNull;

public class ProductDetailsController {

    @FXML public ImageView productImageView;
    @FXML public Label productNameLabel;
    @FXML public Label productPriceLabel;
    @FXML public Label productCategoryLabel;
    @FXML public Label companyProductPriceLabel;
    @FXML public Label companyProductPriceTitleLabel;
    @FXML public Label descriptionLabel;
    @FXML public Button companyProductPriceButton;
    @FXML public Button companyProductsSortButton;
    @FXML public VBox companyProductsVBox;

    private final CompanyProductRepository companyProductRepository = new CompanyProductRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    private String sortType = "desc";
    private List<CompanyProduct> companyProducts = new ArrayList<>();

    public void initialize() {
        if (Session.getSelectedProduct().isEmpty()) {
            handleUnselectedProductOrCompany();
        }

        Product selectedProduct = Session.getSelectedProduct().get();

        productImageView.setImage(selectedProduct.getImage());
        productNameLabel.setText(selectedProduct.getName());

        if (!isNull(selectedProduct.getDescription())) {
            descriptionLabel.setText(selectedProduct.getDescription());
        }

        try {
            productCategoryLabel.setText(categoryRepository.findCategoryHierarchy(selectedProduct.getCategory().getId()));
            companyProducts = new ArrayList<>(companyProductRepository.findByProductId(selectedProduct.getId(),
                    CompanyProductRecordType.LATEST_RECORD));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Please try again later.");
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

        productPriceLabel.setText(lowestPrice + "€ - " + highestPrice + "€");

        Optional<Company> selectedCompany = Session.getSelectedCompany();

        if (selectedCompany.isPresent()) {
            Optional<BigDecimal> price = companyProducts.stream()
                    .filter(companyProduct -> Objects.equals(companyProduct.getCompany().getId(), selectedCompany.get().getId()))
                    .map(companyProduct -> companyProduct.getPrice().value())
                    .findFirst();

            price.ifPresent(bigDecimal -> {
                companyProductPriceLabel.setText(bigDecimal + "€");
                companyProductPriceTitleLabel.setText("Price in " + selectedCompany.get().getName());
            });

            if (price.isEmpty()) {
                companyProductPriceLabel.setVisible(false);
                companyProductPriceTitleLabel.setVisible(false);
                companyProductPriceButton.setVisible(false);
            }
        }

        companyProducts.sort((cp1, cp2) -> cp2.getPrice().value().compareTo(cp1.getPrice().value()));
        fillCompanyProductsVBox();
    }

    public void handleCompanySort() {
        if (sortType.equals("asc")) {
            sortType = "desc";
            companyProductsSortButton.setText("Sort by price (asc)");
            companyProducts.sort((cp1, cp2) -> cp2.getPrice().value().compareTo(cp1.getPrice().value()));
        } else {
            sortType = "asc";
            companyProductsSortButton.setText("Sort by price (desc)");
            companyProducts.sort((cp1, cp2) -> cp1.getPrice().value().compareTo(cp2.getPrice().value()));
        }

        fillCompanyProductsVBox();
    }

    public void fillCompanyProductsVBox() {
        companyProductsVBox.getChildren().clear();

        for (CompanyProduct companyProduct : companyProducts) {
            Label companyNameLabel = new Label(companyProduct.getCompany().getName() + ": ");
            companyNameLabel.getStyleClass().add("formLabel");

            Label priceLabel = new Label(companyProduct.getPrice().value() + "€");
            priceLabel.setStyle("-fx-font-weight: bold");
            priceLabel.getStyleClass().add("formLabel");

            HBox productBox = new HBox(companyNameLabel, priceLabel);

            productBox.setCursor(javafx.scene.Cursor.HAND);
            productBox.setOnMouseClicked(event ->
                    SceneLoader.loadProductCompanyGraphPopupScene("company_product_graph",
                            "Product graph",
                            companyProduct.getCompany()));

            companyProductsVBox.getChildren().add(productBox);
        }
    }

    public void handleChangePrice() {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene("change_product_price", "Change price");

        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog("Error fetching data from popup window");
            return;
        }

        ChangeProductPriceController controller = loader.get().getController();

        if (Session.getSelectedProduct().isEmpty() || Session.getSelectedCompany().isEmpty()) {
            handleUnselectedProductOrCompany();
            return;
        }

        Price price = controller.getNewPrice();

        if (!isNull(price)) {
            try {
                companyProductRepository.updatePrice(
                        Session.getSelectedCompany().get().getId(),
                        Session.getSelectedProduct().get().getId(),
                        controller.getNewPrice());
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Please try again later.");
                return;
            }

            initialize();
        }
    }

    public void handleUnselectedProductOrCompany() {
        AlertDialog.showErrorDialog("Please select a product first.");
        SceneLoader.loadScene("dashboard", "Dashboard");
    }
}