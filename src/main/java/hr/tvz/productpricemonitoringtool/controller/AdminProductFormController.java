package hr.tvz.productpricemonitoringtool.controller;

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

public class AdminProductFormController {

    @FXML public TextField nameTextField;
    @FXML public TextField descriptionTextField;
    @FXML public Label selectedCategoryLabel;
    @FXML public Button submitButton;

    private Category category;
    private Optional<Product> productEdit;

    private final ProductRepository productRepository = new ProductRepository();

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

    public void handleSubmit() {
        String validationError = validateFormData();

        if (validationError.isEmpty()) {
            Product product = new Product.Builder(null)
                    .name(nameTextField.getText())
                    .category(category)
                    .description(descriptionTextField.getText())
                    .build();

            try {
                if (productEdit.isPresent()) {
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

    public String validateFormData() {
        if (nameTextField.getText().trim().isEmpty())
            return "Name field must be filled!";
        if (isNull(category))
            return "Category must be selected/created";

        return "";
    }
}
