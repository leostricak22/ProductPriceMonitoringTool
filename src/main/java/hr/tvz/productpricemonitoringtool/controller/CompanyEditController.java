package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.UserFileRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Controller for the company edit view.
 * Handles the form for editing companies.
 * Validates the input and saves the company to the database.
 * @see Company
 * @see Address
 * @see AddressRepository
 * @see CompanyRepository
 * @see UserFileRepository
 * @see AlertDialog
 */
public class CompanyEditController {

    @FXML public Label sectionTitleLabel;
    @FXML public TextField nameTextField;
    @FXML public Label addressLabel;

    private Address address;

    private final Logger logger = LoggerFactory.getLogger(CompanyEditController.class);
    private final AddressRepository addressRepository = new AddressRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();
    private final UserFileRepository userFileRepository = new UserFileRepository();

    /**
     * Initializes the view.
     * Fills the form with the company data if present.
     */
    public void initialize() {
        Company company = Session.getSelectedCompany().orElseThrow(() ->
                new AuthenticationException("Company is not selected"));

        sectionTitleLabel.setText("Edit Company: " + company.getName());

        nameTextField.setText(company.getName());
        address = company.getAddress();
        addressLabel.setText(address.getAddress());
    }

    /**
     * Handles the form submit.
     * Validates the input and saves the company to the database.
     */
    public void handleEdit() {
        String name = nameTextField.getText();

        String validationMessage = validateInput(name);
        if(!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            logger.error("Validation failed while creating a Company: {}", validationMessage);
            return;
        }

        try {
            address = addressRepository.save(address);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        Company company = new Company.Builder(getCompany().getId(), name)
                .address(address)
                .build();

        try {
            companyRepository.update(company);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        Session.getLoggedInUser().ifPresentOrElse(user -> {
            Optional<User> optionalUser = userFileRepository.findById(user.getId());

            if (optionalUser.isEmpty()) {
                throw new AuthenticationException("User not found");
            }

            Session.setLoggedInUser(optionalUser.get());
        }, () -> {
            throw new AuthenticationException("User is not logged in");
        });

        Session.setSelectedCompany(company);

        AlertDialog.showInformationDialog("Success", "Company created successfully");
        SceneLoader.loadScene("company_dashboard", "Dashboard");
    }

    /**
     * Handles the address creation.
     * Opens the address form and saves the created address.
     */
    public void handleMapButton() {
        Optional<Address> optionalAddress = MapUtil.handleMapPick(address, addressLabel);
        optionalAddress.ifPresent(addressOptional -> this.address = addressOptional);
    }

    /**
     * Validates the input.
     * @param name Company name
     * @return Error message if the input is not valid
     */
    private String validateInput(String name) {
        if(name.trim().isEmpty()) {
            return "Name is required";
        }
        if (isNull(address)) {
            return "Address must be selected/created";
        }

        return "";
    }

    /**
     * Gets the selected company.
     * @return Selected company
     */
    private Company getCompany() {
        return Session.getSelectedCompany().orElseThrow(() ->
                new AuthenticationException("Company is not selected"));
    }
}
