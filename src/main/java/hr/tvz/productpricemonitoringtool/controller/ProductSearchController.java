package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.FilterSearch;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.thread.FetchProductsByCategoriesThread;
import hr.tvz.productpricemonitoringtool.thread.FetchProductsByFilterThread;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

public class ProductSearchController {

    @FXML public GridPane mainPane;
    @FXML public HBox categoryHBox;
    @FXML public FlowPane productsFlowPane;
    @FXML public Label hierarchyLabel;
    @FXML public Label categoriesTitleLabel;
    @FXML public Label noCategoriesTitleLabel;

    private Optional<Category> parentCategory = Optional.empty();
    private FilterSearch filterSearch;
    private final List<Product> products = new ArrayList<>();

    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final CompanyProductRepository companyProductRepository = new CompanyProductRepository();

    public void initialize(Optional<Category> parentCategory) {
        this.parentCategory = parentCategory;

        loadProducts();
    }

    public void initialize(FilterSearch filterSearch) {
        this.parentCategory = Optional.empty();
        this.filterSearch = filterSearch;

        loadFilteredProducts();
    }

    public void loadProducts() {
        List<Category> categories = new ArrayList<>();
        String hierarchy = "";
        Boolean success = false;

        productsFlowPane.getChildren().clear();
        products.clear();

        FetchProductsByCategoriesThread fetchProductsThread = new FetchProductsByCategoriesThread(productRepository,
                categoryRepository,
                products,
                categories,
                parentCategory,
                hierarchy,
                success);
        Thread thread = new Thread(fetchProductsThread);
        thread.start();

        ProgressBarUtil progressBar = new ProgressBarUtil(mainPane);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BigDecimal counter = BigDecimal.ZERO;
                Instant startTime = Instant.now();

                while (thread.isAlive()) {
                    try {
                        Thread.sleep(50);

                        if (Instant.now().minusSeconds(Constants.MAX_DB_CONNECTION_WAIT_TIME_IN_SECONDS).isAfter(startTime)) {
                            thread.interrupt();
                        }

                        counter = ProgressBarUtil.imitateProgressCounter(counter);

                        progressBar.update(BigDecimal.valueOf(counter.divide(BigDecimal.valueOf(100),
                                RoundingMode.HALF_UP).doubleValue()));
                    } catch (InterruptedException e) {
                        thread.interrupt();
                        return null;
                    }
                }

                return null;
            }

            @Override
            protected void succeeded() {
                if (Boolean.FALSE.equals(fetchProductsThread.getSuccess())) {
                    progressBar.remove();
                    AlertDialog.showErrorDialog("Error while fetching products from database");
                    return;
                }

                super.succeeded();

                Platform.runLater(() -> progressBar.update(BigDecimal.ONE));

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
                    progressBar.remove();
                    categoryHBox.getChildren().clear();
                    productsFlowPane.getChildren().clear();

                    createCategorySectionButtons(categories);
                    addProductPanes(products);
                    hierarchyLabel.setText(fetchProductsThread.getHierarchy());
                }));
                timeline.setCycleCount(1);
                timeline.play();
            }

            @Override
            protected void failed() {
                super.failed();
                progressBar.remove();
                AlertDialog.showErrorDialog("Error while fetching products from database");
            }
        };

        new Thread(task).start();
    }

    private void loadFilteredProducts() {
        categoryHBox.getChildren().clear();
        productsFlowPane.getChildren().clear();

        FetchProductsByFilterThread fetchProductsThread = new FetchProductsByFilterThread(filterSearch,
                companyProductRepository,
                products);
        Thread thread = new Thread(fetchProductsThread);
        thread.start();

        ProgressBarUtil progressBar = new ProgressBarUtil(mainPane);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                BigDecimal counter = BigDecimal.ZERO;
                Instant startTime = Instant.now();

                while (thread.isAlive()) {
                    try {
                        Thread.sleep(50);

                        if (Instant.now().minusSeconds(Constants.MAX_DB_CONNECTION_WAIT_TIME_IN_SECONDS).isAfter(startTime)) {
                            thread.interrupt();
                        }

                        counter = ProgressBarUtil.imitateProgressCounter(counter);

                        progressBar.update(BigDecimal.valueOf(counter.divide(BigDecimal.valueOf(100),
                                RoundingMode.HALF_UP).doubleValue()));
                    } catch (InterruptedException e) {
                        thread.interrupt();
                        return null;
                    }
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                progressBar.update(BigDecimal.ONE);
                progressBar.remove();

                categoryHBox.getChildren().clear();
                productsFlowPane.getChildren().clear();

                addProductPanes(products);
                hierarchyLabel.setText("Filtered products");
                hierarchyLabel.getStyleClass().add("sectionHeading");

                categoriesTitleLabel.setVisible(false);
                noCategoriesTitleLabel.setVisible(true);
            }

            @Override
            protected void failed() {
                super.failed();
                progressBar.remove();
                AlertDialog.showErrorDialog("Error while fetching filtered products from database");
            }
        };

        new Thread(task).start();
    }

    private void createCategorySectionButtons(List<Category> categories) {
        if (parentCategory.isPresent()) {
            Button backButton = new Button("<");
            backButton.setOnAction(event -> initialize(parentCategory.flatMap(Category::getParentCategory)));
            categoryHBox.getChildren().add(backButton);
        }

        for (Category category : categories) {
            Button categoryButton = new Button(category.getName());
            categoryButton.setOnAction(event -> initialize(Optional.of(category)));
            categoryHBox.getChildren().add(categoryButton);
        }
    }

    private void addProductPanes(List<Product> products) {
        products.forEach(product -> {
            Pane productPane = createProductPane(product);
            productsFlowPane.getChildren().add(productPane);
        });
    }

    private GridPane createProductPane(Product product) {
        GridPane pane = new GridPane();

        ImageView imageView = new ImageView(product.getImage());
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        pane.getChildren().add(imageView);

        List<CompanyProduct> companyProducts = new ArrayList<>();
        try {
            companyProducts = new ArrayList<>(companyProductRepository.findByProductId(product.getId(),
                    CompanyProductRecordType.LATEST_RECORD));
        } catch (
        DatabaseConnectionActiveException e) {
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

        VBox namePriceBox = new VBox();
        Label nameLabel = new Label(product.getName());
        Label priceLabel = new Label(lowestPrice + "€ - " + highestPrice + "€");

        nameLabel.getStyleClass().add("product-name-label");
        priceLabel.getStyleClass().add("product-price-label");

        namePriceBox.getChildren().add(nameLabel);
        namePriceBox.getChildren().add(priceLabel);

        pane.getChildren().add(namePriceBox);

        pane.getStyleClass().add("product-pane");
        GridPane.setRowIndex(imageView, 0);
        GridPane.setRowIndex(namePriceBox, 1);

        pane.setOnMouseClicked(event -> {
            Session.setSelectedProduct(product);
            SceneLoader.loadScene("product_details", "Product Details");
        });

        return pane;
    }
}
