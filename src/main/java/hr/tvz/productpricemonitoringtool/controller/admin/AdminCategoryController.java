package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.SearchController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ComboBoxUtil;
import hr.tvz.productpricemonitoringtool.util.PopupSceneLoader;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Controller for the admin category view.
 * Handles the category search and filtering.
 * @see Category
 * @see CategoryRepository
 * @see SearchController
 * @see AlertDialog
 * @see PopupSceneLoader
 */
public class AdminCategoryController implements SearchController {

    @FXML public TextField idTextField;
    @FXML public TextField nameTextField;
    @FXML public ComboBox<Category> parentCategoryComboBox;

    @FXML public TableView<Category> categoryTableView;
    @FXML public TableColumn<Category, Long> idTableColumn;
    @FXML public TableColumn<Category, String> nameTableColumn;
    @FXML public TableColumn<Category, String> parentCategoryTableColumn;

    @FXML public Label removeFiltersLabel;

    private final CategoryRepository categoryRepository = new CategoryRepository();

    /**
     * Initializes the view.
     * Loads the categories and parent categories.
     * Sets the cell value factories for the table columns.
     */
    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        nameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));


        parentCategoryTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getParentCategory().isEmpty() ? "-"
                                : cellData.getValue().getParentCategory().get().getName()));

        ComboBoxUtil.comboBoxStringConverter(parentCategoryComboBox);
        try {
            parentCategoryComboBox.setItems(FXCollections.observableArrayList(categoryRepository.findAll()));
        } catch (Exception e) {
            AlertDialog.showErrorDialog("Error while loading categories.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        removeFiltersLabel.setVisible(false);
        filter();
    }

    /**
     * Filters the categories based on the input values.
     * Shows the filter label if any filter is applied.
     */
    @Override
    public void filter() {
        List<Category> categories = new ArrayList<>();
        try {
            categories = new ArrayList<>(categoryRepository.findAll());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading categories.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        String idValue = this.idTextField.getText();
        String nameValue = this.nameTextField.getText();
        Category parentCategoryValue = this.parentCategoryComboBox.getValue();

        showFilterLabel();

        categoryTableView.setItems(FXCollections.observableArrayList(categories.stream()
                .filter(category -> idValue.isEmpty() || category.getId().toString().equals(idValue))
                .filter(category -> nameValue.isEmpty() || category.getName().contains(nameValue))
                .filter(category -> isNull(parentCategoryValue) ||
                        category.getParentCategory().isPresent() &&
                                category.getParentCategory().get().equals(parentCategoryValue))
                .toList()));
    }

    /**
     * Removes the filters and clears the input fields.
     * Refreshes the table.
     */
    @Override
    public void removeFilters() {
        idTextField.clear();
        nameTextField.clear();
        parentCategoryComboBox.setValue(null);

        filter();
    }

    /**
     * Handles the add new button click.
     * Loads the category popup scene.
     * Refreshes the table.
     */
    @Override
    public void handleAddNewButtonClick() {
        PopupSceneLoader.loadCategoryPopupScene("admin_category_form", "Add category", Optional.empty());
        filter();
    }

    /**
     * Handles the edit button click.
     * Loads the category popup scene with the selected category.
     * Refreshes the table.
     */
    @Override
    public void handleEditButtonClick() {
        Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedCategory)) {
            AlertDialog.showErrorDialog("No category selected");
            return;
        }

        PopupSceneLoader.loadCategoryPopupScene("admin_category_form", "Edit category", Optional.of(selectedCategory));
        filter();
    }

    /**
     * Handles the delete button click.
     * Deletes the selected category.
     * Refreshes the table.
     */
    @Override
    public void handleDeleteButtonClick() {
        Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedCategory)) {
            AlertDialog.showErrorDialog("No category selected");
            return;
        }

        Optional<ButtonType> result = AlertDialog.showConfirmationDialog(
                "Are you sure you want to delete the selected product?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryRepository.delete(selectedCategory);
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error while deleting a company.");
            }
        }

        filter();
    }

    /**
     * Shows the filter label if any filter is applied.
     */
    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
                !idTextField.getText().isEmpty() ||
                !nameTextField.getText().isEmpty() ||
                parentCategoryComboBox.getValue() != null);
    }
}