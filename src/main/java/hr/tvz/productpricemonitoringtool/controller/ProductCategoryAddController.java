package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.TreeViewUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Controller for the product category add view.
 * Handles the form for adding product categories.
 * Validates the input and saves the category to the database.
 * @see Category
 * @see CategoryRepository
 * @see AlertDialog
 * @see Constants
 */
public class ProductCategoryAddController {

    @FXML public TreeView<Category> categoryTreeView;
    @FXML public TextField newCategoryNameTextField;
    @FXML public Label categoryHierarchyLabel;
    @FXML public Label resetCategoryHierarchyLabel;

    private Category savedCategory;

    private final CategoryRepository categoryRepository = new CategoryRepository();
    private String categoryHierarchy = "";

    /**
     * Initializes the view.
     * Fills the tree view with the categories.
     * Handles the tree view selection.
     */
    public void initialize() {
        TreeViewUtil.treeViewStringConverter(categoryTreeView);

        categoryTreeView.setRoot(new TreeItem<>());
        categoryTreeView.getRoot().setExpanded(true);

        try {
            TreeViewUtil.populateCategoryTreeView(categoryTreeView, categoryRepository);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
        }

        categoryTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> {
                    try {
                        handleTreeItemSelection(newItem);
                    } catch (DatabaseConnectionActiveException e) {
                        AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
                    }
                }
        );
    }

    /**
     * Handles the tree view selection.
     * Sets the category hierarchy label.
     * @param newItem the selected tree item
     */
    private void handleTreeItemSelection(TreeItem<Category> newItem) throws DatabaseConnectionActiveException {
        if (isNull(newItem)) {
            return;
        }

        categoryHierarchy = categoryRepository.findCategoryHierarchy(newItem.getValue().getId());
        categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX + categoryHierarchy);

        if (!newCategoryNameTextField.getText().isBlank()) {
            categoryHierarchyLabel.setText(categoryHierarchyLabel.getText() + " -> " + newCategoryNameTextField.getText());
        }
    }

    /**
     * Handles the new category name change.
     * Sets the category hierarchy label.
     */
    public void handleNewCategoryNameChange() {
        String textFieldValue = newCategoryNameTextField.getText();

        if (textFieldValue.isEmpty()) {
            resetCategoryHierarchyLabel.setVisible(false);
            categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX + categoryHierarchy);
            return;
        }

        resetCategoryHierarchyLabel.setVisible(true);
        categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX + categoryHierarchy);

        if (!categoryHierarchy.isEmpty()) {
            categoryHierarchyLabel.setText(categoryHierarchyLabel.getText() + " -> ");
        }

        categoryHierarchyLabel.setText(categoryHierarchyLabel.getText() + textFieldValue);
    }

    /**
     * Handles the reset category hierarchy button.
     * Clears the new category name text field.
     * Clears the category hierarchy label.
     */
    public void handleResetCategoryHierarchy() {
        newCategoryNameTextField.clear();
        resetCategoryHierarchyLabel.setVisible(false);
        categoryHierarchy = "";
        categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX);
        categoryTreeView.getSelectionModel().clearSelection();
    }

    /**
     * Handles the save button.
     * Validates the input.
     * Saves the category to the database.
     */
    public void handleSave() {
        String newCategoryName = newCategoryNameTextField.getText();
        TreeItem<Category> selectedCategoryTreeItem = categoryTreeView.getSelectionModel().getSelectedItem();

        if (isNull(selectedCategoryTreeItem) && newCategoryName.isEmpty()) {
            AlertDialog.showErrorDialog("Category must be selected/created");
            return;
        }

        Optional<Category> selectedCategory;
        if (isNull(selectedCategoryTreeItem) || isNull(selectedCategoryTreeItem.getValue())) {
            selectedCategory = Optional.empty();
        } else {
            selectedCategory = Optional.of(selectedCategoryTreeItem.getValue());
        }

        if(!newCategoryName.isEmpty()) {
            savedCategory = new Category.Builder(null)
                    .name(newCategoryName)
                    .parentCategory(selectedCategory)
                    .build();
        } else {
            if (selectedCategory.isEmpty()) {
                AlertDialog.showErrorDialog("Category must be selected/created");
                return;
            }

            savedCategory = selectedCategory.get();
        }

        Stage stage = (Stage) newCategoryNameTextField.getScene().getWindow();
        stage.close();
    }

    /**
     * Gets the saved category.
     * @return the saved category
     */
    public Category getSavedCategory() {
        return savedCategory;
    }

    /**
     * Gets the category hierarchy from the label.
     * @return the category hierarchy
     */
    public String getHierarchyFromLabel() {
        return categoryHierarchyLabel.getText();
    }
}
