package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.isNull;

public class CompanyAddController {

    @FXML public TextField nameTextField;
    @FXML public Label addressLabel;

    private Address address;

    private final Logger logger = LoggerFactory.getLogger(CompanyAddController.class);
    private final AddressRepository addressRepository = new AddressRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();

    public void handleCreate() {
        String name = nameTextField.getText();

        String validationMessage = validateInput(name);
        if(!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog("Validation error", validationMessage);
            logger.error("Validation failed while creating a Company: {}", validationMessage);
            return;
        }

        User user = Session.getLoggedInUser().orElseThrow(() -> new AuthenticationException("User is not logged in"));

        try {
            address = addressRepository.save(address);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        Company company = new Company.Builder(0L, name)
                .address(address)
                .build();

        try {
            companyRepository.save(company);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        try {
            companyRepository.addUser(user.getId(), company.getId());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        try {
            user.setCompanies(companyRepository.findAllByUserId(user.getId()));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        Session.setLoggedInUser(user);

        AlertDialog.showInformationDialog("Success", "Company created successfully");
        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    public void handleMapButton() {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene("map_picker", "Map Picker");

        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    "Error fetching data from popup window");
            return;
        }

        MapPickerController controller = loader.get().getController();
        if(!isNull(controller.getAddress())) {
            address = controller.getAddress();
            addressLabel.setText(address.getAddress());
        }
    }

    private String validateInput(String name) {
        if(name.trim().isEmpty()) {
            return "Name is required";
        }
        if (isNull(address)) {
            return "Address must be selected/created";
        }

        return "";
    }
}
