package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductWriteRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ComboBoxUtil;
import hr.tvz.productpricemonitoringtool.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Objects.isNull;

public class AdminCompanyProductFormController {

    @FXML public ComboBox<Company> companyComboBox;
    @FXML public ComboBox<Product> productComboBox;
    @FXML public TextField priceTextField;
    @FXML public Button submitButton;

    private Optional<CompanyProductDBO> companyProductEdit;

    private final CompanyProductWriteRepository companyProductWriteRepository = new CompanyProductWriteRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();
    private final ProductRepository productRepository = new ProductRepository();

    public void initialize(Optional<CompanyProductDBO> companyProduct) {
        ComboBoxUtil.comboBoxStringConverter(companyComboBox);
        ComboBoxUtil.comboBoxStringConverter(productComboBox);

        try {
            companyComboBox.getItems().setAll(companyRepository.findAll());
            productComboBox.getItems().setAll(productRepository.findAll());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error fetching data");
            return;
        }

        if (companyProduct.isPresent()) {
            CompanyProductDBO cp = companyProduct.get();

            try {
                companyComboBox.setValue(companyRepository.findById(cp.getCompanyId()).orElseThrow(() -> new RepositoryQueryException("Error fetching data")));
                productComboBox.setValue(productRepository.findById(cp.getProductId()).orElseThrow(() -> new RepositoryAccessException("Error fetching data")));
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error fetching data");
                return;
            }

            submitButton.setText("Edit");
            priceTextField.setText(cp.getPrice().value().toString());

            this.companyProductEdit = companyProduct;
        } else {
            this.companyProductEdit = Optional.empty();
        }
    }

    public void handleSubmit() {
        String validationMessage = validateInput();

        if (!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            return;
        }

        try {
            if (!isNull(companyProductEdit) && companyProductEdit.isPresent()) {
                CompanyProductDBO cp = companyProductEdit.get();
                cp.setCompanyId(companyComboBox.getValue().getId());
                cp.setProductId(productComboBox.getValue().getId());
                cp.setPrice(new Price(new BigDecimal(priceTextField.getText())));

                companyProductWriteRepository.update(cp);
            } else {
                CompanyProduct cp = new CompanyProduct.Builder(0L)
                        .company(companyComboBox.getValue())
                        .product(productComboBox.getValue())
                        .price(new Price(new BigDecimal(priceTextField.getText())))
                        .build();

                companyProductWriteRepository.save(cp);
            }

            Stage stage = (Stage) priceTextField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            AlertDialog.showErrorDialog("Error saving company product");
        }
    }

    public String validateInput() {
        if (isNull(companyComboBox.getValue())) {
            return "Company is required";
        }

        if (isNull(productComboBox.getValue())) {
            return "Product is required";
        }

        if (priceTextField.getText().isEmpty()) {
            return "Price is required";
        }

        if (!ValidationUtil.isPositiveBigDecimal(priceTextField.getText())) {
            return "Price must be a positive number";
        }

        return "";
    }
}
