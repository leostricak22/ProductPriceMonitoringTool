package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.SearchController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
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
 * Controller for the admin products view.
 * Handles the product search and filtering.
 * @see Product
 * @see ProductRepository
 * @see Category
 * @see CategoryRepository
 * @see SearchController
 * @see AlertDialog
 * @see PopupSceneLoader
 * @see SceneLoader
 * @see ComboBoxUtil
 */
public class AdminProductsController implements SearchController {

    @FXML public TextField idTextField;
    @FXML public TextField nameTextField;
    @FXML public TextField descriptionTextField;

    @FXML public ComboBox<Category> categoryComboBox;

    @FXML public TableView<Product> productTableView;
    @FXML public TableColumn<Product, Long> idTableColumn;
    @FXML public TableColumn<Product, String> nameTableColumn;
    @FXML public TableColumn<Product, String> descriptionTableColumn;
    @FXML public TableColumn<Product, String> categoryTableColumn;

    @FXML public Label removeFiltersLabel;

    private final ProductRepository productRepository = new ProductRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    /**
     * Initializes the view.
     * Loads the products and categories.
     * Sets the cell value factories for the table columns.
     */
    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        nameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        descriptionTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        categoryTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getName()));

        ComboBoxUtil.comboBoxStringConverter(categoryComboBox);
        try {
            List<Category> categories = new ArrayList<>(categoryRepository.findAll());
            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading categories.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        removeFiltersLabel.setVisible(false);
        filter();
    }

    /**
     * Filters the products based on the input values.
     * Shows the filter label if any filter is applied.
     */
    @Override
    public void filter() {
        List<Product> products = new ArrayList<>();
        try {
            products = new ArrayList<>(productRepository.findAll());
            products.sort((p1, p2) -> (int) (p1.getId() - p2.getId()));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading products.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        String idValue = this.idTextField.getText();
        String nameValue = this.nameTextField.getText();
        String descriptionValue = this.descriptionTextField.getText();
        Category categoryValue = this.categoryComboBox.getValue();

        showFilterLabel();

        productTableView.setItems(FXCollections.observableArrayList(products.stream()
                .filter(product -> idValue.isEmpty() || product.getId().toString().equals(idValue))
                .filter(product -> nameValue.isEmpty() || product.getName().contains(nameValue))
                .filter(product -> descriptionValue.isEmpty() ||
                        (!isNull(product.getDescription()) &&
                                product.getDescription().toLowerCase().contains(descriptionValue.toLowerCase())))
                .filter(product -> isNull(categoryValue) || product.getCategory().equals(categoryValue))
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
        descriptionTextField.clear();
        categoryComboBox.getSelectionModel().clearSelection();

        filter();
    }

    /**
     * Handles the add new button click.
     * Opens a popup window for adding a new product.
     * Refreshes the table.
     */
    @Override
    public void handleAddNewButtonClick() {
        PopupSceneLoader.loadProductFormPopupScene(
                "admin_product_form", "Edit product", Optional.empty());

        filter();
    }

    /**
     * Handles the edit button click.
     * Opens a popup window for editing the selected product.
     * Refreshes the table.
     */
    @Override
    public void handleEditButtonClick() {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (isNull(selectedProduct)) {
            AlertDialog.showErrorDialog("No product selected");
            return;
        }

        PopupSceneLoader.loadProductFormPopupScene(
                "admin_product_form", "Edit product", Optional.of(selectedProduct));

        filter();
    }

    /**
     * Handles the delete button click.
     * Deletes the selected product.
     * Refreshes the table.
     */
    @Override
    public void handleDeleteButtonClick() {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (isNull(selectedProduct)) {
            AlertDialog.showErrorDialog("No product selected");
            return;
        }

        Optional<ButtonType> result = AlertDialog.showConfirmationDialog(
                "Are you sure you want to delete the selected product?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productRepository.delete(selectedProduct);
                filter();
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error while deleting product.");
            }
        }
    }

    /**
     * Shows the filter label if any filter is applied.
     */
    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
                !idTextField.getText().isEmpty() ||
                !nameTextField.getText().isEmpty() ||
                !descriptionTextField.getText().isEmpty() ||
                categoryComboBox.getValue() != null
        );
    }
}
