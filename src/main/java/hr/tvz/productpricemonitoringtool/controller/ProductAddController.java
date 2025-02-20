package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.*;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Objects.isNull;

/**
 * Controller for the product add view.
 * Handles the form for adding products.
 * Validates the input and saves the product to the database.
 * @see Category
 * @see Product
 * @see CompanyProduct
 * @see Price
 * @see CategoryRepository
 * @see ProductRepository
 * @see AlertDialog
 * @see Session
 * @see Constants
 */
public class ProductAddController {

    @FXML public GridPane mainPane;
    @FXML public TextField nameTextField;
    @FXML public TextField priceTextField;
    @FXML public Label categoryHierarchyLabel;
    @FXML public Label removePickedImageLabel;
    @FXML public ImageView productImageView;
    @FXML public TextArea descriptionTextArea;

    private Category category;
    private String imageUrl;
    boolean isImageChanged = false;

    private final Logger logger = LoggerFactory.getLogger(ProductAddController.class);
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final ProductRepository productRepository = new ProductRepository();

    /**
     * Initializes the view.
     * Sets the default image and hides the remove image label.
     */
    public void initialize() {
        productImageView.setImage(new Image(Constants.NO_IMAGE_URL));
        removePickedImageLabel.setVisible(false);
    }

    /**
     * Handles the image pick button.
     * Opens the file picker and sets the image to the product image view.
     * Sets the image changed flag to true.
     */
    public void handleImagePick() {
        Image image;
        Optional<File> selectedFileOptional = FileUtil.pickFile(List.of("*.jpg", "*.jpeg", "*.png"));
        if (selectedFileOptional.isEmpty())
            return;

        imageUrl = selectedFileOptional.get().toURI().toString();
        image = FileUtil.cropImageToSquare(new Image(selectedFileOptional.get().toURI().toString()));
        productImageView.setImage(image);
        isImageChanged = true;
        removePickedImageLabel.setVisible(true);
    }

    /**
     * Handles the remove picked image button.
     * Sets the product image view to the default image.
     * Sets the image changed flag to false.
     */
    public void handleRemovePickedImage() {
        productImageView.setImage(new Image(Constants.NO_IMAGE_URL));
        isImageChanged = false;
        removePickedImageLabel.setVisible(false);
    }

    /**
     * Handles the add product button.
     * Validates the input and saves the product to the database.
     * Redirects to the company products list view.
     */
    public void handleAddProduct() {
        if (Session.getSelectedCompany().isEmpty()) {
            AlertDialog.showErrorDialog("No company selected. Please select company first.");
            SceneLoader.loadScene("company_select", "Select company");
        }
        Company selectedCompany = Session.getSelectedCompany().get();

        String error = validateFormData();
        if (!error.isEmpty()) {
            AlertDialog.showErrorDialog(error);
            return;
        }

        try {
            Category newCategory = this.category;
            if (isNull(newCategory.getId())) {
                newCategory = categoryRepository.save(newCategory);
            }

            CompanyProduct companyProduct = new CompanyProduct.Builder(0L)
                    .company(selectedCompany)
                    .price(new Price(new BigDecimal(priceTextField.getText())))
                    .build();

            Product product = new Product.Builder(null)
                    .name(nameTextField.getText())
                    .category(newCategory)
                    .companyProducts(Set.of(companyProduct))
                    .description(descriptionTextArea.getText())
                    .build();

            productRepository.save(product);

            if (isImageChanged) {
                FileUtil.saveImage(imageUrl, "files/product/" + product.getId()
                        + imageUrl.substring(imageUrl.lastIndexOf(".")));
            }

            SceneLoader.loadScene("company_products_list", "Company products");
        } catch (DatabaseConnectionActiveException e) {
            logger.error("Error saving category", e);
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
        }
    }

    /**
     * Validates the form data.
     * Checks if the name, category, and price fields are filled.
     * Checks if the price is a positive number.
     * @return the error message if the validation fails
     */
    private String validateFormData() {
        if (nameTextField.getText().trim().isEmpty())
            return "Name field must be filled!";
        if (isNull(category))
            return "Category must be selected/created";
        if (priceTextField.getText().trim().isEmpty())
            return "Price field must be filled!";
        if (!ValidationUtil.isPositiveBigDecimal(priceTextField.getText()))
            return "Price must be a positive number!";

        return "";
    }

    /**
     * Handles the select category button.
     * Opens the category add popup window.
     * Sets the selected category and the category hierarchy label.
     */
    public void handleSelectCategory() {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene("product_category_add", "Add category");

        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog("Error fetching data from popup window");
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
            this.category = newCategory;
        }
    }
}
