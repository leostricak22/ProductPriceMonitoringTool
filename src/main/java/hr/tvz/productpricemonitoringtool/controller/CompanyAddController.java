package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.UserCompanyRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
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
    private final UserCompanyRepository userCompanyRepository = new UserCompanyRepository();

    public void handleCreate() {
        String name = nameTextField.getText();

        String validationMessage = validateInput(name);
        if(!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            logger.error("Validation failed while creating a Company: {}", validationMessage);
            return;
        }

        User user = Session.getLoggedInUser().orElseThrow(() -> new AuthenticationException("User is not logged in"));

        try {
            address = addressRepository.save(address);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        Company company = new Company.Builder(0L, name)
                .address(address)
                .joinCode(Hash.generateJoinCode())
                .build();

        try {
            companyRepository.save(company);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        try {
            userCompanyRepository.addUser(user.getId(), company.getId());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        try {
            user.setCompanies(companyRepository.findAllByUserId(user.getId()));
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
            logger.error(Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE, e.getMessage());
            return;
        }

        Session.setLoggedInUser(user);

        AlertDialog.showInformationDialog("Success", "Company created successfully");
        SceneLoader.loadScene("dashboard", "Dashboard");
    }

    public void handleMapButton() {
        Optional<Address> optionalAddress = MapUtil.handleMapPick(address, addressLabel);
        optionalAddress.ifPresent(addressOptional -> this.address = addressOptional);
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
