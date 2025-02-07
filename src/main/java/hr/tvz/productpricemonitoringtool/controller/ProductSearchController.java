package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductSearchController {

    @FXML public FlowPane categoryFlowPane;
    @FXML public FlowPane productsFlowPane;
    @FXML public Label hierarchyLabel;

    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final ProductRepository productRepository = new ProductRepository();

    public void initialize(Optional<Category> parentCategory) {
        categoryFlowPane.getChildren().clear();
        List<Category> categories = new ArrayList<>();

        try {
            categories.addAll(categoryRepository.findAllByParentCategory(parentCategory));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            handleDashboardRedirect();
        }

        categories.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));

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

        parentCategory.ifPresentOrElse(
                category -> {
                    try {
                        hierarchyLabel.setText(categoryRepository.findCategoryHierarchy(category.getId()));
                    } catch (DatabaseConnectionActiveException e) {
                        AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                                Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
                        handleDashboardRedirect();
                    }
                },
                () -> hierarchyLabel.setText("")
        );

        productsFlowPane.getChildren().clear();

        List<Product> products = new ArrayList<>();
        try {
            products = new ArrayList<>(productRepository.findAllByCategory(parentCategory));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error", "Database connection is active. Please try again later.");
            handleDashboardRedirect();
        }

        products.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));

        products.forEach(product -> {
            Pane productPane = createProductPane(product);
            productsFlowPane.getChildren().add(productPane);
        });

    }

    public GridPane createProductPane(Product product) {
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

    public void handleDashboardRedirect() {
        SceneLoader.loadScene("dashboard", "Dashboard");
    }
}
