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
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompanyAddController {

    @FXML public TextField nameTextField;
    @FXML public TextField streetTextField;
    @FXML public TextField houseNumberTextField;
    @FXML public TextField cityTextField;
    @FXML public TextField postalCodeTextField;
    @FXML public TextField countryTextField;

    private final Logger logger = LoggerFactory.getLogger(CompanyAddController.class);
    private final AddressRepository addressRepository = new AddressRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();

    public void handleCreate() {
        String name = nameTextField.getText();
        String street = streetTextField.getText();
        String houseNumber = houseNumberTextField.getText();
        String city = cityTextField.getText();
        String postalCode = postalCodeTextField.getText();
        String country = countryTextField.getText();

        String validationMessage = validateInput(name, street, houseNumber, city, postalCode, country);
        if(!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog("Validation error", validationMessage);
            logger.error("Validation failed while creating a Company: {}", validationMessage);
            return;
        }

        User user = Session.getLoggedInUser().orElseThrow(() -> new AuthenticationException("User is not logged in"));

        Address address = new Address.Builder(0L)
                .street(street)
                .houseNumber(houseNumber)
                .city(city)
                .postalCode(postalCode)
                .country(country)
                .build();

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
        SceneLoader.loadPopupScene("map_picker", "Map Picker");
    }

    private String validateInput(String name, String street, String houseNumber, String city, String postalCode, String country) {
        if(name.trim().isEmpty()) {
            return "Name is required";
        }
        if(street.trim().isEmpty()) {
            return "Street is required";
        }
        if(houseNumber.trim().isEmpty()) {
            return "House number is required";
        }
        if(city.trim().isEmpty()) {
            return "City is required";
        }
        if(postalCode.trim().isEmpty()) {
            return "Postal code is required";
        }
        if(country.trim().isEmpty()) {
            return "Country is required";
        }

        return "";
    }
}
