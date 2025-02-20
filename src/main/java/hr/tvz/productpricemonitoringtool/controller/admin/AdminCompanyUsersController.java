package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.SearchController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.model.dbo.UserCompanyDBO;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.UserCompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.UserFileRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ComboBoxUtil;
import hr.tvz.productpricemonitoringtool.util.PopupSceneLoader;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
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

/**
 * Controller for the admin company users view.
 * Handles the company user search and filtering.
 * @see UserCompanyDBO
 * @see Company
 * @see User
 * @see CompanyRepository
 * @see UserFileRepository
 * @see UserCompanyRepository
 * @see SearchController
 * @see AlertDialog
 * @see PopupSceneLoader
 * @see SceneLoader
 */
public class AdminCompanyUsersController implements SearchController {

    @FXML public TextField idTextField;
    @FXML public ComboBox<Company> companyComboBox;
    @FXML public ComboBox<User> userComboBox;
    @FXML public DatePicker dateFromPicker;
    @FXML public DatePicker dateToPicker;

    @FXML public TableView<UserCompanyDBO> companyUsersTableView;
    @FXML public TableColumn<UserCompanyDBO, Long> idTableColumn;
    @FXML public TableColumn<UserCompanyDBO, String> companyTableColumn;
    @FXML public TableColumn<UserCompanyDBO, String> userTableColumn;
    @FXML public TableColumn<UserCompanyDBO, String> createdAtTableColumn;

    @FXML public Label removeFiltersLabel;

    private final UserFileRepository userFileRepository = new UserFileRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();
    private final UserCompanyRepository userCompanyRepository = new UserCompanyRepository();

    /**
     * Initializes the view.
     * Loads the company users and companies.
     * Sets the cell value factories for the table columns.
     */
    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        companyTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCompanyId().toString()));

        userTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUserId().toString()));

        createdAtTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedAt().format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm"))));

        try {
            companyComboBox.setItems(FXCollections.observableArrayList(companyRepository.findAll()));
            userComboBox.setItems(FXCollections.observableArrayList(userFileRepository.findAll()));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Failed to fetch data from database.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        ComboBoxUtil.comboBoxStringConverter(companyComboBox);
        ComboBoxUtil.comboBoxStringConverter(userComboBox);

        filter();
    }

    /**
     * Filters the company users based on the input values.
     * Shows the filter label if any filter is applied.
     */
    @Override
    public void filter() {
        List<UserCompanyDBO> companyUsers;
        try {
            companyUsers = new ArrayList<>(userCompanyRepository.findAllUserCompanyDBO());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Failed to fetch data from database.");
            return;
        }
        companyUsers.sort((cu1, cu2) -> (int) (cu1.getId() - cu2.getId()));

        String idValue = idTextField.getText();
        Company companyValue = companyComboBox.getValue();
        User userValue = userComboBox.getValue();
        Optional<LocalDateTime> dateFromValue = !isNull(this.dateFromPicker.getValue()) ? Optional.of(this.dateFromPicker.getValue().atStartOfDay()) : Optional.empty();
        Optional<LocalDateTime> dateToValue = !isNull(this.dateToPicker.getValue()) ? Optional.of(this.dateToPicker.getValue().atStartOfDay()) : Optional.empty();

        showFilterLabel();

        companyUsersTableView.setItems(FXCollections.observableArrayList(companyUsers.stream()
                .filter(companyUser -> idValue.isEmpty() || String.valueOf(companyUser.getId()).contains(idValue))
                .filter(companyUser -> isNull(companyValue) || companyUser.getCompanyId().equals(companyValue.getId()))
                .filter(companyUser -> isNull(userValue) || companyUser.getUserId().equals(userValue.getId()))
                .filter(companyUser -> dateFromValue.isEmpty() || companyUser.getCreatedAt().isAfter(dateFromValue.get()))
                .filter(companyUser -> dateToValue.isEmpty() || companyUser.getCreatedAt().isBefore(dateToValue.get()))
                .toList()));
    }

    /**
     * Removes the filters and clears the input fields.
     * Refreshes the table.
     */
    @Override
    public void removeFilters() {
        idTextField.clear();
        companyComboBox.setValue(null);
        userComboBox.setValue(null);
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);

        showFilterLabel();
        filter();
    }

    /**
     * Handles the add new button click.
     * Loads the company user form popup scene.
     */
    @Override
    public void handleAddNewButtonClick() {
        PopupSceneLoader.loadCompanyUsersFormPopupScene("admin_company_staff_form", "Add company user", Optional.empty());
        filter();
    }

    /**
     * Handles the edit button click.
     * Loads the company user form popup scene with the selected company user.
     */
    @Override
    public void handleEditButtonClick() {
        UserCompanyDBO selectedCompanyUser = companyUsersTableView.getSelectionModel().getSelectedItem();

        if (isNull(selectedCompanyUser)) {
            AlertDialog.showErrorDialog("No company user selected");
            return;
        }

        PopupSceneLoader.loadCompanyUsersFormPopupScene("admin_company_staff_form", "Edit company user", Optional.of(selectedCompanyUser));
        filter();
    }

    /**
     * Handles the delete button click.
     * Deletes the selected company user.
     */
    @Override
    public void handleDeleteButtonClick() {
        UserCompanyDBO selectedCompanyUser = companyUsersTableView.getSelectionModel().getSelectedItem();

        if (isNull(selectedCompanyUser)) {
            AlertDialog.showErrorDialog("No company user selected");
            return;
        }

        Optional<ButtonType> result = AlertDialog.showConfirmationDialog(
                "Are you sure you want to delete the selected product?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userCompanyRepository.deleteUserCompany(selectedCompanyUser);
                filter();
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error while deleting product.");
            }
        }

        filter();
    }

    /**
     * Shows the filter label if any filter is applied.
     */
    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
                !idTextField.getText().isEmpty() ||
                companyComboBox.getValue() != null ||
                userComboBox.getValue() != null ||
                dateFromPicker.getValue() != null ||
                dateToPicker.getValue() != null);
    }
}
