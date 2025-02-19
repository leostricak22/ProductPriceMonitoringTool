package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Hash;
import hr.tvz.productpricemonitoringtool.util.MapUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

import static java.util.Objects.isNull;

public class AdminCompanyFormController {

    @FXML public TextField nameTextField;
    @FXML public Label addressLabel;
    @FXML public Button submitButton;

    private Address address;
    private Optional<Company> company;

    private final CompanyRepository companyRepository = new CompanyRepository();
    private final AddressRepository addressRepository = new AddressRepository();

    public void initialize(Optional<Company> company) {
        if(company.isPresent()) {
            Company companyOptional = company.get();
            nameTextField.setText(companyOptional.getName());
            address = companyOptional.getAddress();
            addressLabel.setText(address.getAddress());
            submitButton.setText("Update");

            this.company = company;
            return;
        }

        addressLabel.setText("No address selected");
    }

    public void handleMapButton() {
        Optional<Address> optionalAddress = MapUtil.handleMapPick(address, addressLabel);
        optionalAddress.ifPresent(addressOptional -> this.address = addressOptional);
    }

    public void handleSubmit() {
        String validationMessage = validateInput(nameTextField.getText());
        if(!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            return;
        }

        try {
            if (!isNull(company)) {
                Company companyOptional = company.get();
                companyOptional.setName(nameTextField.getText());

                if (!address.equals(companyOptional.getAddress())) {
                    address = addressRepository.save(address);
                }

                companyOptional.setAddress(address);
                companyRepository.update(companyOptional);
            } else {
                address = addressRepository.save(address);

                companyRepository.save(new Company.Builder(0L, nameTextField.getText())
                        .address(address)
                        .joinCode(Hash.generateJoinCode())
                        .build());
            }
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while saving a company");
        }

        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }

    private String validateInput(String name) {
        if(name.isEmpty()) {
            return "Name cannot be empty";
        }

        if(isNull(address)) {
            return "Address must be selected";
        }

        return "";
    }
}
