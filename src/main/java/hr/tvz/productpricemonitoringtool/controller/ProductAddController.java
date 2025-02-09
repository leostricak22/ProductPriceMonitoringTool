package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ProductAddController {

    @FXML public GridPane mainPane;
    @FXML public TextField nameLabel;

    public void initialize() {
        // i will do this later
    }

    public void handleImagePick() {
        // i will do this later
    }

    public void handleAddProduct() {
        // i will do this later
    }

    public void handleSelectCategory() {
        FXMLLoader loader = SceneLoader.loadPopupScene("product_category_add", "Add category");
        if (loader != null) {
            ProductCategoryAddController controller = loader.getController();
            Category newCategory = controller.getSavedCategory();
            if (newCategory != null) {
                System.out.println("New category added: " + newCategory.getName());
            } else {
                System.out.println("No category was saved.");
            }
        }
    }
}
