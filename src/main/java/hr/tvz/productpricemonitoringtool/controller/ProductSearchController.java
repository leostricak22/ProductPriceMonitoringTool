package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

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
        List<Category> categories = categoryRepository.findAllByParentCategory(parentCategory);

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
                category -> hierarchyLabel.setText(categoryRepository.findCategoryHierarchy(category.getId())),
                () -> hierarchyLabel.setText("")
        );

        productsFlowPane.getChildren().clear();

        productRepository.findAllByCategory(parentCategory).forEach(product -> {
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
}
