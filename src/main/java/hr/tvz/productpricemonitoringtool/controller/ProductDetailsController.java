package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductReadRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductWriteRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.isNull;

/**
 * Controller for the product details view.
 * Handles the product details and company products list.
 * @see CompanyProduct
 * @see CompanyProductReadRepository
 * @see CompanyProductWriteRepository
 * @see CategoryRepository
 * @see AlertDialog
 * @see Session
 * @see SceneLoader
 */
public class ProductDetailsController {

    @FXML
    public ImageView productImageView;
    @FXML
    public Label productNameLabel;
    @FXML
    public Label productPriceLabel;
    @FXML
    public Label productCategoryLabel;
    @FXML
    public Label companyProductPriceLabel;
    @FXML
    public Label companyProductPriceTitleLabel;
    @FXML
    public Label descriptionLabel;
    @FXML
    public Button companyProductPriceButton;
    @FXML
    public Button companyProductsSortButton;
    @FXML
    public VBox companyProductsVBox;
    @FXML
    public LineChart<String, BigDecimal> productLineChart;

    private final CompanyProductWriteRepository companyProductWriteRepository = new CompanyProductWriteRepository();
    private final CompanyProductReadRepository companyProductReadRepository = new CompanyProductReadRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    private String sortType = "desc";
    private List<CompanyProduct> companyProducts = new ArrayList<>();
    private List<CompanyProduct> companyProductsSort = new ArrayList<>();

    /**
     * Initializes the view.
     * Fills the product details and company products list.
     * Redirects to the dashboard if no product is selected.
     */
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
            companyProducts = new ArrayList<>(companyProductReadRepository.findByProductId(selectedProduct.getId(),
                    CompanyProductRecordType.LATEST_RECORD));
            companyProductsSort = new ArrayList<>(companyProductReadRepository.findByProductId(selectedProduct.getId(),
                    CompanyProductRecordType.LATEST_RECORD));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.TRY_AGAIN_LATER_ERROR_MESSAGE);
            SceneLoader.loadScene(Constants.DASHBOARD_FILE_NAME, Constants.DASHBOARD_TITLE);
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
                companyProductPriceTitleLabel.setText("Your company also sells this product?");
                companyProductPriceLabel.setText("Add it to the list!");
                companyProductPriceButton.setText("Add your price");
                companyProductPriceButton.setOnAction(event -> handleAddPrice());
            }
        } else {
            hideCompanyProductPrice();
        }

        companyProducts.sort((cp1, cp2) -> cp2.getPrice().value().compareTo(cp1.getPrice().value()));
        fillCompanyProductsVBox();
        try {
            createProductLineChart(selectedProduct.getId());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Please try again later.");
            SceneLoader.loadScene(Constants.DASHBOARD_FILE_NAME, Constants.DASHBOARD_TITLE);
        }
    }

    /**
     * Handles the company products sort button.
     * Sorts the company products by price.
     */
    public void handleCompanySort() {
        if (sortType.equals("asc")) {
            sortType = "desc";
            companyProductsSortButton.setText("Sort by price (asc)");
            companyProductsSort.sort((cp1, cp2) -> cp2.getPrice().value().compareTo(cp1.getPrice().value()));
        } else {
            sortType = "asc";
            companyProductsSortButton.setText("Sort by price (desc)");
            companyProductsSort.sort((cp1, cp2) -> cp1.getPrice().value().compareTo(cp2.getPrice().value()));
        }

        fillCompanyProductsVBox();
    }

    /**
     * Fills the company products list.
     * Redirects to the company product graph view on product click.
     */
    public void fillCompanyProductsVBox() {
        companyProductsVBox.getChildren().clear();

        for (CompanyProduct companyProduct : companyProducts) {
            Label companyNameLabel = new Label(companyProduct.getCompany().getName() + ": ");
            companyNameLabel.getStyleClass().add("formLabel");

            Label priceLabel = new Label(companyProduct.getPrice().value() + "€");
            priceLabel.setStyle("-fx-font-weight: bold");
            priceLabel.getStyleClass().add("formLabel");

            HBox productBox = new HBox(companyNameLabel, priceLabel);
            productBox.getStyleClass().add("product-company-box-container");

            productBox.setCursor(javafx.scene.Cursor.HAND);
            productBox.setOnMouseClicked(event ->
                    PopupSceneLoader.loadProductCompanyGraphPopupScene("company_product_graph",
                            "Product graph",
                            companyProduct.getCompany()));

            companyProductsVBox.getChildren().add(productBox);
        }
    }

    /**
     * Handles the company product price button.
     */
    public void handleChangePrice() {
        handleSavePrice("change_product_price");
    }

    /**
     * Handles the add price button.
     */
    public void handleAddPrice() {
        handleSavePrice("add_product_price");
    }

    /**
     * Handles the save price action.
     * Saves the new price to the database.
     * @param sceneName the name of the popup scene
     */
    public void handleSavePrice(String sceneName) {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene(sceneName, "Price");

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
                companyProductWriteRepository.updatePrice(
                        Session.getSelectedCompany().get().getId(),
                        Session.getSelectedProduct().get().getId(),
                        controller.getNewPrice());
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Please try again later.");
                return;
            }

            SceneLoader.loadScene("product_details", "Product Details");
        }
    }

    /**
     * Handles the unselected product or company.
     * Redirects to the dashboard.
     */
    public void handleUnselectedProductOrCompany() {
        AlertDialog.showErrorDialog("Please select a product first.");
        SceneLoader.loadScene(Constants.DASHBOARD_FILE_NAME, Constants.DASHBOARD_TITLE);
    }

    /**
     * Creates the product line chart.
     * @param productId the product id
     * @throws DatabaseConnectionActiveException if the database connection is active
     */
    public void createProductLineChart(Long productId) throws DatabaseConnectionActiveException {
        companyProducts = new ArrayList<>(companyProductReadRepository.findByProductId(
                productId,
                CompanyProductRecordType.ALL_RECORDS));
        companyProducts.sort(Comparator.comparing(CompanyProduct::getCreatedAt));

        productLineChart.getData().clear();

        Map<LocalDateTime, BigDecimal> highestPrices = new LinkedHashMap<>();
        Map<LocalDateTime, BigDecimal> lowestPrices = new LinkedHashMap<>();

        Map<Long, BigDecimal> currentPrices = new HashMap<>();

        for (CompanyProduct cp : companyProducts) {
            Long companyId = cp.getCompany().getId();
            BigDecimal price = cp.getPrice().value();

            currentPrices.put(companyId, price);

            BigDecimal currentHighest = currentPrices.values().stream()
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);
            BigDecimal currentLowest = currentPrices.values().stream()
                    .min(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);

            highestPrices.put(cp.getCreatedAt(), currentHighest);
            lowestPrices.put(cp.getCreatedAt(), currentLowest);
        }

        XYChart.Series<String, BigDecimal> highestPriceSeries = new XYChart.Series<>();
        highestPriceSeries.setName("Highest Price");

        for (Map.Entry<LocalDateTime, BigDecimal> entry : highestPrices.entrySet()) {
            highestPriceSeries.getData().add(new XYChart.Data<>(
                    entry.getKey().format(DateTimeFormatter.ofPattern("dd.MM.yy. HH:mm")), entry.getValue()));
        }

        XYChart.Series<String, BigDecimal> lowestPriceSeries = new XYChart.Series<>();
        lowestPriceSeries.setName("Lowest Price");

        for (Map.Entry<LocalDateTime, BigDecimal> entry : lowestPrices.entrySet()) {
            lowestPriceSeries.getData().add(new XYChart.Data<>(
                    entry.getKey().format(DateTimeFormatter.ofPattern("dd.MM.yy. HH:mm")), entry.getValue()));
        }

        productLineChart.getData().addAll(highestPriceSeries, lowestPriceSeries);
    }

    /**
     * Hides the company product price.
     */
    private void hideCompanyProductPrice() {
        companyProductPriceLabel.setVisible(false);
        companyProductPriceTitleLabel.setVisible(false);
        companyProductPriceButton.setVisible(false);
    }
}