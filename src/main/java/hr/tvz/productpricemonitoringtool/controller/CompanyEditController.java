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

public class CompanyEditController {

    @FXML public Label sectionTitleLabel;
    @FXML public TextField nameTextField;
    @FXML public Label addressLabel;

    private Address address;

    private final Logger logger = LoggerFactory.getLogger(CompanyEditController.class);
    private final AddressRepository addressRepository = new AddressRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();
    private final UserFileRepository userFileRepository = new UserFileRepository();

    public void initialize() {
        Company company = Session.getSelectedCompany().orElseThrow(() ->
                new AuthenticationException("Company is not selected"));

        sectionTitleLabel.setText("Edit Company: " + company.getName());

        nameTextField.setText(company.getName());
        address = company.getAddress();
        addressLabel.setText(address.getAddress());
    }

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

        companyRepository.update(company);

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

    private Company getCompany() {
        return Session.getSelectedCompany().orElseThrow(() ->
                new AuthenticationException("Company is not selected"));
    }
}
