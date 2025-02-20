package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.SearchController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
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

public class AdminCompanyController implements SearchController {

    @FXML
    public TextField idTextField;
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField joinCodeTextField;

    @FXML
    public ComboBox<Address> addressComboBox;

    @FXML
    public TableView<Company> companyTableView;
    @FXML
    public TableColumn<Company, Long> idTableColumn;
    @FXML
    public TableColumn<Company, String> nameTableColumn;
    @FXML
    public TableColumn<Company, String> addressTableColumn;
    @FXML
    public TableColumn<Company, String> joinCodeTableColumn;

    @FXML
    public Label removeFiltersLabel;

    private final CompanyRepository companyRepository = new CompanyRepository();
    private final AddressRepository addressRepository = new AddressRepository();

    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        nameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        addressTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAddress().getAddress()));

        joinCodeTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getJoinCode()));

        ComboBoxUtil.comboBoxStringConverter(addressComboBox);
        try {
            addressComboBox.setItems(FXCollections.observableArrayList(addressRepository.findAll()));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading addresses.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        removeFiltersLabel.setVisible(false);
        filter();
    }

    @Override
    public void filter() {
        companyTableView.getItems().clear();
        List<Company> companies = new ArrayList<>();
        try {
            companies = new ArrayList<>(companyRepository.findAll());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading companies.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        String idValue = this.idTextField.getText();
        String nameValue = this.nameTextField.getText();
        String joinCodeValue = this.joinCodeTextField.getText();
        Address addressValue = this.addressComboBox.getValue();

        showFilterLabel();

        companyTableView.setItems(FXCollections.observableArrayList(companies.stream()
                .filter(company -> idValue.isEmpty() || company.getId().toString().equals(idValue))
                .filter(company -> nameValue.isEmpty() || company.getName().contains(nameValue))
                .filter(company -> joinCodeValue.isEmpty() ||
                        (!isNull(company.getJoinCode()) &&
                                company.getJoinCode().toLowerCase().contains(joinCodeValue.toLowerCase())))
                .filter(company -> isNull(addressValue) || company.getAddress().equals(addressValue))
                .sorted((c1, c2) -> c1.getId().compareTo(c2.getId()))
                .toList()));
    }

    @Override
    public void removeFilters() {
        idTextField.clear();
        nameTextField.clear();
        joinCodeTextField.clear();
        addressComboBox.getSelectionModel().clearSelection();

        filter();
    }

    @Override
    public void handleAddNewButtonClick() {
        PopupSceneLoader.loadCompanyFormPopupScene(
                "admin_company_form", "Add new company", Optional.empty());
        filter();
    }

    @Override
    public void handleEditButtonClick() {
        Company selectedCompany = companyTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedCompany)) {
            AlertDialog.showErrorDialog("Please select a company to edit.");
            return;
        }

        PopupSceneLoader.loadCompanyFormPopupScene(
                "admin_company_form", "Edit company", Optional.of(selectedCompany));
        filter();
    }

    @Override
    public void handleDeleteButtonClick() {
        Company selectedCompany = companyTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedCompany)) {
            AlertDialog.showErrorDialog("Please select a company to delete.");
            return;
        }

        Optional<ButtonType> result = AlertDialog.showConfirmationDialog(
                "Are you sure you want to delete the selected product?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                companyRepository.delete(selectedCompany);
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error while deleting a company.");
            }
        }

        filter();
    }

    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
                !idTextField.getText().isEmpty() ||
                        !nameTextField.getText().isEmpty() ||
                        !joinCodeTextField.getText().isEmpty() ||
                        !isNull(addressComboBox.getValue()));
    }
}