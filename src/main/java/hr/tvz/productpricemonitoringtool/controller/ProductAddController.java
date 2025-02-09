package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.isNull;

public class ProductAddController {

    private static final Logger logger = LoggerFactory.getLogger(ProductAddController.class);
    @FXML public GridPane mainPane;
    @FXML public TextField nameTextField;
    @FXML public Label categoryHierarchyLabel;

    private Optional<Category> category = Optional.empty();

    public void initialize() {
        // i will do this later
    }

    public void handleImagePick() {
        // i will do this later
    }

    public void handleAddProduct() {
        String error = validateFormData();

        logger.error(error);
    }

    private String validateFormData() {
        if (nameTextField.getText().trim().isEmpty())
            return "Name field must be filled!";
        if (category.isEmpty())
            return "Category must be selected/created";

        return "";
    }

    public void handleSelectCategory() {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene("product_category_add", "Add category");

        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    "Error fetching data from popup window");
            return;
        }

        ProductCategoryAddController controller = loader.get().getController();
        Category newCategory = controller.getSavedCategory();
        String categoryHierarchy = controller.getHierarchyFromLabel();

        if (!isNull(categoryHierarchy)) {
            categoryHierarchyLabel.setText(categoryHierarchy.substring(10));
            categoryHierarchyLabel.setVisible(true);
        }

        if (!isNull(newCategory)) {
            this.category = Optional.of(newCategory);
        }
    }
}
