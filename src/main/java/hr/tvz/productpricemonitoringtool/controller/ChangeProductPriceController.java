package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
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

public class ChangeProductPriceController {

    @FXML public TextField oldPriceTextField;
    @FXML public TextField newPriceTextField;

    private Price newPrice;

    private final CompanyProductRepository companyProductRepository = new CompanyProductRepository();

    public void initialize() {
        Optional<Product> selectedProduct = Session.getSelectedProduct();
        Optional<Company> selectedCompany = Session.getSelectedCompany();

        if (selectedProduct.isEmpty() || selectedCompany.isEmpty()) {
            AlertDialog.showErrorDialog("Please select a product and company first.");
            return;
        }

        Set<CompanyProduct> companyProducts;
        try {
            companyProducts = companyProductRepository.findByProductId(selectedProduct.get().getId(),
                    CompanyProductRecordType.LATEST_RECORD);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            return;
        }

        companyProducts.stream()
                .filter(companyProduct -> companyProduct.getCompany().getId().equals(selectedCompany.get().getId()))
                .findFirst()
                .ifPresent(companyProduct -> oldPriceTextField.setText(companyProduct.getPrice().value().toString()));
    }

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
