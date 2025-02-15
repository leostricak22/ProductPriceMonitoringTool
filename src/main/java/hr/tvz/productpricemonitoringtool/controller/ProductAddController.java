package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.*;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
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

public class ProductAddController {

    @FXML public GridPane mainPane;
    @FXML public TextField nameTextField;
    @FXML public TextField priceTextField;
    @FXML public Label categoryHierarchyLabel;
    @FXML public Label removePickedImageLabel;
    @FXML public ImageView productImageView;

    private Category category;
    private String imageUrl;
    boolean isImageChanged = false;

    private final Logger logger = LoggerFactory.getLogger(ProductAddController.class);
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final ProductRepository productRepository = new ProductRepository();

    public void initialize() {
        productImageView.setImage(new Image(Constants.NO_IMAGE_URL));
        removePickedImageLabel.setVisible(false);
    }

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

    public void handleRemovePickedImage() {
        productImageView.setImage(new Image(Constants.NO_IMAGE_URL));
        isImageChanged = false;
        removePickedImageLabel.setVisible(false);
    }

    public void handleAddProduct() {
        if (Session.getSelectedCompany().isEmpty()) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    "No company selected. Please select company first.");
            SceneLoader.loadScene("company_select", "Select company");
        }
        Company selectedCompany = Session.getSelectedCompany().get();

        String error = validateFormData();
        if (!error.isEmpty()) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE, error);
            return;
        }

        try {
            Category newCategory = this.category;
            if (isNull(newCategory.getId())) {
                newCategory = categoryRepository.save(newCategory);
            }

            CompanyProduct companyProduct = new CompanyProduct.Builder()
                    .company(selectedCompany)
                    .price(new Price(new BigDecimal(priceTextField.getText())))
                    .build();

            Product product = new Product.Builder(null)
                    .name(nameTextField.getText())
                    .category(newCategory)
                    .companyProducts(Set.of(companyProduct))
                    .build();

            productRepository.save(product);

            if (isImageChanged) {
                FileUtil.saveImage(imageUrl, "files/product/" + product.getId()
                        + imageUrl.substring(imageUrl.lastIndexOf(".")));
            }

            SceneLoader.loadScene("dashboard", "Dashboard");
        } catch (DatabaseConnectionActiveException e) {
            logger.error("Error saving category", e);
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
        }
    }

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
            this.category = newCategory;
        }
    }
}
