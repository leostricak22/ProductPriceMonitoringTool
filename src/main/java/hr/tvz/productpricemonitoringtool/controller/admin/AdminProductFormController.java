package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.ProductCategoryAddController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Controller for the admin product form view.
 * Handles the form for adding and editing products.
 * Validates the input and saves the product to the database.
 * @see Product
 * @see ProductRepository
 * @see AlertDialog
 * @see ProductCategoryAddController
 * @see SceneLoader
 */
public class AdminProductFormController {

    @FXML public TextField nameTextField;
    @FXML public TextField descriptionTextField;
    @FXML public Label selectedCategoryLabel;
    @FXML public Button submitButton;

    private Category category;
    private Optional<Product> productEdit;

    private final ProductRepository productRepository = new ProductRepository();

    /**
     * Initializes the view.
     * If the product is present, fills the form with the product data.
     * @param product Optional product to edit
     */
    public void initialize(Optional<Product> product) {
        if (product.isPresent()) {
            Product p = product.get();
            nameTextField.setText(p.getName());
            descriptionTextField.setText(p.getDescription());
            selectedCategoryLabel.setText(p.getCategory().getName());
            selectedCategoryLabel.setVisible(true);
            this.category = p.getCategory();

            submitButton.setText("Update");
            this.productEdit = Optional.of(p);
        } else {
            selectedCategoryLabel.setVisible(false);
        }

        selectedCategoryLabel.setText("No category selected");
    }

    /**
     * Handles the pick category button.
     * Opens a popup window for adding a new category.
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
     * Validates the input and saves the product to the database.
     * Closes the form window.
     */
    public void handleSubmit() {
        String validationError = validateFormData();

        if (validationError.isEmpty()) {
            Product product = new Product.Builder(null)
                    .name(nameTextField.getText())
                    .category(category)
                    .description(descriptionTextField.getText())
                    .build();

            try {
                if (!isNull(productEdit) && productEdit.isPresent()) {
                    product.setId(productEdit.get().getId());
                    product.setCompanyProducts(productEdit.get().getCompanyProducts());
                    productRepository.update(product);
                } else {
                    productRepository.save(product);
                }

                Stage stage = (Stage) descriptionTextField.getScene().getWindow();
                stage.close();
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error saving product");
            }
        } else {
            AlertDialog.showErrorDialog(validateFormData());
        }
    }

    /**
     * Validates the form data.
     * @return Error message if the input is not valid, empty string otherwise
     */
    public String validateFormData() {
        if (nameTextField.getText().trim().isEmpty())
            return "Name field must be filled!";
        if (isNull(category))
            return "Category must be selected/created";

        return "";
    }
}
