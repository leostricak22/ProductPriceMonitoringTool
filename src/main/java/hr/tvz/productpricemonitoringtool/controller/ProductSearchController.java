package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.util.List;
import java.util.Optional;

public class ProductSearchController {

    @FXML public FlowPane categoryFlowPane;
    @FXML public Label hierarchyLabel;

    private final CategoryRepository categoryRepository = new CategoryRepository();

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
    }
}
