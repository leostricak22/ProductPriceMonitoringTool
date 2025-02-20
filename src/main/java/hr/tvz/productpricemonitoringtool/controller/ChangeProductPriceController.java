package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductReadRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.Session;
import hr.tvz.productpricemonitoringtool.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * Controller for the change product price view.
 * Handles the form for changing the product price.
 * Validates the input and saves the new price.
 * @see CompanyProduct
 * @see CompanyProductReadRepository
 * @see AlertDialog
 */
public class ChangeProductPriceController {

    @FXML public TextField oldPriceTextField;
    @FXML public TextField newPriceTextField;

    private Price newPrice;

    private final CompanyProductReadRepository companyProductReadRepository = new CompanyProductReadRepository();

    /**
     * Initializes the view.
     * Fills the form with the old price of the product.
     */
    public void initialize() {
        Optional<Product> selectedProduct = Session.getSelectedProduct();
        Optional<Company> selectedCompany = Session.getSelectedCompany();

        if (selectedProduct.isEmpty() || selectedCompany.isEmpty()) {
            AlertDialog.showErrorDialog("Please select a product and company first.");
            return;
        }

        Set<CompanyProduct> companyProducts;
        try {
            companyProducts = companyProductReadRepository.findByProductId(selectedProduct.get().getId(),
                    CompanyProductRecordType.LATEST_RECORD);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            return;
        }

        if (!isNull(companyProducts)) {
            companyProducts.stream()
                .filter(companyProduct -> companyProduct.getCompany().getId().equals(selectedCompany.get().getId()))
                .findFirst()
                .ifPresent(companyProduct -> oldPriceTextField.setText(companyProduct.getPrice().value().toString()));
        }
    }

    /**
     * Handles the form submit.
     * Validates the input and saves the new price.
     */
    public void handleSave() {
        if (newPriceTextField.getText().isEmpty()) {
            AlertDialog.showErrorDialog("Please enter new price.");
            return;
        }

        if (!ValidationUtil.isPositiveBigDecimal(newPriceTextField.getText())) {
            AlertDialog.showErrorDialog("Please enter valid price.");
            return;
        }

        this.newPrice = new Price(new BigDecimal(newPriceTextField.getText()));

        Stage stage = (Stage) newPriceTextField.getScene().getWindow();
        stage.close();
    }

    public Price getNewPrice() {
        return newPrice;
    }
}
