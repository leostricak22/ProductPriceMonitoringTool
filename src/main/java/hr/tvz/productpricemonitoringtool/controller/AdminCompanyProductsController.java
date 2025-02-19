package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ComboBoxUtil;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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

    private final CompanyProductRepository companyProductRepository = new CompanyProductRepository();
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
            companyProducts = new ArrayList<>(companyProductRepository.findAllDBO());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading company products.");
            return;
        }

        filter();
    }

    @Override
    public void filter() {
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

    }

    @Override
    public void handleEditButtonClick() {

    }

    @Override
    public void handleDeleteButtonClick() {

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