package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.fxml.FXML;
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
        SceneLoader.loadPopupScene("product_category_add", "Add category");
    }
}
