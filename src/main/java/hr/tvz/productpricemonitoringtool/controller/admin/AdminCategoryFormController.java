package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.ProductCategoryAddController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * Controller for the admin category form view.
 * Handles the form for adding and editing categories.
 * Validates the input and saves the category to the database.
 * @see Category
 * @see CategoryRepository
 * @see AlertDialog
 * @see ProductCategoryAddController
 * @see SceneLoader
 */
public class AdminCategoryFormController {

    @FXML public Button submitButton;
    @FXML public Label selectedCategoryLabel;

    private Category category;
    private Optional<Category> categoryEdit;

    private final CategoryRepository categoryRepository = new CategoryRepository();

    /**
     * Initializes the view.
     * If the category is present, fills the form with the category data.
     * @param category Optional category to edit
     */
    public void initialize(Optional<Category> category) {
        if (category.isPresent()) {
            Category c = category.get();
            selectedCategoryLabel.setText(c.getName());
            selectedCategoryLabel.setVisible(true);
            this.category = c;

            submitButton.setText("Update");
            this.categoryEdit = Optional.of(c);
        } else {
            selectedCategoryLabel.setVisible(false);
        }

        selectedCategoryLabel.setText("No category selected");
    }

    /**
     * Handles the pick category button.
     * Opens the popup window for adding a new category.
     * Sets the selected category label.
     */
    public void handlePickCategory() {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene("product_category_add", "Add category");

        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog("Error fetching data from popup window");
            return;
        }

        ProductCategoryAddController controller = loader.get().getController();
        Category newCategory = controller.getSavedCategory();
        String categoryHierarchy = controller.getHierarchyFromLabel();

        if (!isNull(categoryHierarchy)) {
            selectedCategoryLabel.setText(categoryHierarchy.substring(10));
            selectedCategoryLabel.setVisible(true);
        }

        if (!isNull(newCategory)) {
            this.category = newCategory;
        }
    }

    /**
     * Handles the submit button action.
     * Validates the input and saves the category to the database.
     * Closes the window after saving.
     * Shows an error dialog if the category already exists in the database.
     * Shows an error dialog if the category is not selected.
     */
    public void handleSubmit() {
        if (isNull(category)) {
            AlertDialog.showErrorDialog("Category not selected");
            return;
        }

        try {
            Set<Category> allCategories = categoryRepository.findAll();
            if ((!isNull(categoryEdit) && !categoryEdit.get().equals(category) && allCategories.stream().anyMatch(c -> c.getName().equals(category.getName())
                    && c.getParentCategory().equals(category.getParentCategory())))
                || isNull(categoryEdit) && allCategories.stream().anyMatch(c -> c.getName().equals(category.getName())
                    && c.getParentCategory().equals(category.getParentCategory()))
            ) {
                AlertDialog.showErrorDialog("Category already exist in the database.");
                return;
            }

            if (!isNull(categoryEdit)) {
                category.setId(categoryEdit.get().getId());
                categoryRepository.update(category);
            } else {
                categoryRepository.save(category);
            }
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Database connection is active");
            return;
        }

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}