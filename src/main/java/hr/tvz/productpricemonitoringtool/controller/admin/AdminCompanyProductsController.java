package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.SearchController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductReadRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductWriteRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class AdminCompanyProductsController implements SearchController {

    @FXML public TextField idTextField;
    @FXML public ComboBox<Company> companyComboBox;
    @FXML public ComboBox<Product> productComboBox;
    @FXML public DatePicker dateFromPicker;
    @FXML public DatePicker dateToPicker;
    @FXML public TextField priceFromTextField;
    @FXML public TextField priceToTextField;

    @FXML public TableView<CompanyProductDBO> companyProductsTableView;
    @FXML public TableColumn<CompanyProductDBO, Long> idTableColumn;
    @FXML public TableColumn<CompanyProductDBO, String> companyTableColumn;
    @FXML public TableColumn<CompanyProductDBO, String> productTableColumn;
    @FXML public TableColumn<CompanyProductDBO, String> createdAtTableColumn;
    @FXML public TableColumn<CompanyProductDBO, String> priceTableColumn;

    @FXML public Label removeFiltersLabel;

    private List<CompanyProductDBO> companyProducts;

    private final CompanyProductWriteRepository companyProductWriteRepository = new CompanyProductWriteRepository();
    private final CompanyProductReadRepository companyProductReadRepository = new CompanyProductReadRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();

    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        companyTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCompanyId().toString()));

        productTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductId().toString()));

        createdAtTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedAt().format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm"))));

        priceTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrice().value().toString()));

        ComboBoxUtil.comboBoxStringConverter(companyComboBox);
        ComboBoxUtil.comboBoxStringConverter(productComboBox);

        try {
            companyComboBox.setItems(FXCollections.observableArrayList(companyRepository.findAll()));
            productComboBox.setItems(FXCollections.observableArrayList(productRepository.findAll()));
        } catch (Exception e) {
            AlertDialog.showErrorDialog("Error while loading companies or products.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        removeFiltersLabel.setVisible(false);

        try {
            companyProducts = new ArrayList<>(companyProductReadRepository.findAllDBO());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            return;
        }

        filter();
    }

    @Override
    public void filter() {
        companyProducts.sort((p1, p2) -> (int) (p1.getId() - p2.getId()));

        String idValue = this.idTextField.getText();
        Company companyValue = this.companyComboBox.getValue();
        Product productValue = this.productComboBox.getValue();
        Optional<LocalDateTime> dateFromValue = !isNull(this.dateFromPicker.getValue()) ? Optional.of(this.dateFromPicker.getValue().atStartOfDay()) : Optional.empty();
        Optional<LocalDateTime> dateToValue = !isNull(this.dateToPicker.getValue()) ? Optional.of(this.dateToPicker.getValue().atStartOfDay()) : Optional.empty();
        String priceFromValue = this.priceFromTextField.getText();
        String priceToValue = this.priceToTextField.getText();

        showFilterLabel();

        companyProductsTableView.setItems(FXCollections.observableArrayList(companyProducts.stream()
                .filter(companyProduct -> idValue.isEmpty() || companyProduct.getId().toString().equals(idValue))
                .filter(companyProduct -> isNull(companyValue) || companyProduct.getCompanyId().equals(companyValue.getId()))
                .filter(companyProduct -> isNull(productValue) || companyProduct.getProductId().equals(productValue.getId()))
                .filter(companyProduct -> dateFromValue.isEmpty() || !companyProduct.getCreatedAt().isBefore(dateFromValue.get()))
                .filter(companyProduct -> dateToValue.isEmpty() || !companyProduct.getCreatedAt().isAfter(dateToValue.get()))
                .filter(companyProduct -> priceFromValue.isEmpty() || companyProduct.getPrice().value().doubleValue() >= Double.parseDouble(priceFromValue))
                .filter(companyProduct -> priceToValue.isEmpty() || companyProduct.getPrice().value().doubleValue() <= Double.parseDouble(priceToValue))
                .toList()));
    }

    @Override
    public void removeFilters() {
        idTextField.clear();
        companyComboBox.setValue(null);
        productComboBox.setValue(null);
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        priceFromTextField.clear();
        priceToTextField.clear();

        filter();
    }

    @Override
    public void handleAddNewButtonClick() {
        PopupSceneLoader.loadCompanyProductPopupScene(
                "admin_company_product_form", "Add company product", Optional.empty());

        try {
            companyProducts = new ArrayList<>(companyProductReadRepository.findAllDBO());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading company products");
            return;
        }
        filter();
    }

    @Override
    public void handleEditButtonClick() {
        CompanyProductDBO selectedCompanyProduct = companyProductsTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedCompanyProduct)) {
            AlertDialog.showErrorDialog("No company product selected");
            return;
        }

        PopupSceneLoader.loadCompanyProductPopupScene(
                "admin_company_product_form", "Edit company product", Optional.of(selectedCompanyProduct));

        try {
            companyProducts = new ArrayList<>(companyProductReadRepository.findAllDBO());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading company products.");
            return;
        }
        filter();
    }

    @Override
    public void handleDeleteButtonClick() {
        CompanyProductDBO selectedCompanyProduct = companyProductsTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedCompanyProduct)) {
            AlertDialog.showErrorDialog("No company product selected");
            return;
        }

        Optional<ButtonType> result = AlertDialog.showConfirmationDialog(
                "Are you sure you want to delete the selected product?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                companyProductWriteRepository.delete(selectedCompanyProduct.getId());
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error while deleting a company product.");
            }
        }

        filter();

        try {
            companyProducts = new ArrayList<>(companyProductReadRepository.findAllDBO());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading company products.");
            return;
        }
        filter();
    }

    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
                !idTextField.getText().isEmpty() ||
                companyComboBox.getValue() != null ||
                productComboBox.getValue() != null ||
                dateFromPicker.getValue() != null ||
                dateToPicker.getValue() != null ||
                !priceFromTextField.getText().isEmpty() ||
                !priceToTextField.getText().isEmpty());
    }
}