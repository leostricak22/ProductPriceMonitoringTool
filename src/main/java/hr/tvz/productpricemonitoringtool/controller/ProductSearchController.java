package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.thread.FetchProductsByCategoriesThread;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.ProgressBarUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

public class ProductSearchController {

    @FXML public GridPane mainPane;
    @FXML public FlowPane categoryFlowPane;
    @FXML public FlowPane productsFlowPane;
    @FXML public Label hierarchyLabel;

    private Optional<Category> parentCategory = Optional.empty();

    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final ProductRepository productRepository = new ProductRepository();

    public void initialize(Optional<Category> parentCategory) {
        this.parentCategory = parentCategory;

        loadProducts();
    }

    public void loadProducts() {
        List<Product> products = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        String hierarchy = "";
        Boolean success = false;

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
                    categoryFlowPane.getChildren().clear();
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

    private void createCategorySectionButtons(List<Category> categories) {
        if (parentCategory.isPresent()) {
            Button backButton = new Button("<");
            backButton.setOnAction(event -> initialize(parentCategory.flatMap(Category::getParentCategory)));
            categoryFlowPane.getChildren().add(backButton);
        }

        for (Category category : categories) {
            Button categoryButton = new Button(category.getName());
            categoryButton.setOnAction(event -> initialize(Optional.of(category)));
            categoryFlowPane.getChildren().add(categoryButton);
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

        Label label = new Label(product.getName());
        pane.getChildren().add(label);

        pane.getStyleClass().add("product-pane");
        GridPane.setRowIndex(imageView, 0);
        GridPane.setRowIndex(label, 1);

        return pane;
    }
}
