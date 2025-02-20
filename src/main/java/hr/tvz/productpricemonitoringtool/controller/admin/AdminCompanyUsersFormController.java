package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.*;
import hr.tvz.productpricemonitoringtool.model.dbo.UserCompanyDBO;
import hr.tvz.productpricemonitoringtool.repository.*;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ComboBoxUtil;
import hr.tvz.productpricemonitoringtool.util.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Controller for the admin company users form view.
 * Handles the form for adding and editing company users.
 * Validates the input and saves the company user to the database.
 * @see Company
 * @see CompanyRepository
 * @see User
 * @see UserFileRepository
 * @see UserCompanyRepository
 * @see AlertDialog
 */
public class AdminCompanyUsersFormController {

    @FXML public ComboBox<Company> companyComboBox;
    @FXML public ComboBox<User> userComboBox;
    @FXML public Button submitButton;

    private Optional<UserCompanyDBO> userCompanyEdit;

    private final CompanyRepository companyRepository = new CompanyRepository();
    private final UserFileRepository userFileRepository = new UserFileRepository();
    private final UserCompanyRepository userCompanyRepository = new UserCompanyRepository();

    /**
     * Initializes the view.
     * If the company user is present, fills the form with the company user data.
     * @param userCompany Optional company user to edit
     */
    public void initialize(Optional<UserCompanyDBO> userCompany) {
        ComboBoxUtil.comboBoxStringConverter(companyComboBox);
        ComboBoxUtil.comboBoxStringConverter(userComboBox);

        try {
            companyComboBox.getItems().setAll(companyRepository.findAll());
            userComboBox.getItems().setAll(userFileRepository.findAll());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            return;
        }

        if (userCompany.isPresent()) {
            UserCompanyDBO cp = userCompany.get();

            try {
                companyComboBox.setValue(companyRepository.findById(cp.getCompanyId()).orElseThrow(() -> new RepositoryQueryException(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE)));
                userComboBox.setValue(userFileRepository.findById(cp.getUserId()).orElse(null));
                submitButton.setText("Edit");
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
                return;
            }

            this.userCompanyEdit = userCompany;
        } else {
            this.userCompanyEdit = Optional.empty();
        }
    }

    /**
     * Handles the submit button action.
     * Validates the input and saves the company user to the database.
     */
    public void handleSubmit() {
        String validationMessage = validateInput();

        if (!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            return;
        }

        try {
            if (!isNull(userCompanyEdit) && userCompanyEdit.isPresent()) {
                UserCompanyDBO userCompany = userCompanyEdit.get();
                userCompany.setCompanyId(companyComboBox.getValue().getId());
                userCompany.setUserId(userComboBox.getValue().getId());

                userCompanyRepository.updateUserCompany(userCompany);
            } else {
                userCompanyRepository.addUser(companyComboBox.getValue().getId(), userComboBox.getValue().getId());
            }

            Stage stage = (Stage) companyComboBox.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            AlertDialog.showErrorDialog("Error saving company product");
        }
    }

    /**
     * Validates the input.
     * @return Error message if the input is not valid
     */
    public String validateInput() {
        if (isNull(companyComboBox.getValue())) {
            return "Company is required";
        }

        if (isNull(userComboBox.getValue())) {
            return "User is required";
        }

        return "";
    }
}