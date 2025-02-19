package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

public class AdminAddressFormController {

    @FXML public TextField roadTextField;
    @FXML public TextField houseNumberTextField;
    @FXML public TextField cityTextField;
    @FXML public TextField townTextField;
    @FXML public TextField villageTextField;
    @FXML public TextField countryTextField;
    @FXML public TextField longitudeTextField;
    @FXML public TextField latitudeTextField;

    @FXML public Button submitButton;

    private Optional<Address> addressEdit;

    private final AddressRepository addressRepository = new AddressRepository();

    public void initialize(Optional<Address> address) {
        if (address.isPresent()) {
            Address a = address.get();
            roadTextField.setText(a.getRoad());
            houseNumberTextField.setText(a.getHouseNumber());
            cityTextField.setText(a.getCity() == null ? "" : a.getCity());
            townTextField.setText(a.getTown() == null ? "" : a.getTown());
            villageTextField.setText(a.getVillage() == null ? "" : a.getVillage());
            countryTextField.setText(a.getCountry());
            longitudeTextField.setText(a.getLongitude().toString());
            latitudeTextField.setText(a.getLatitude().toString());
            submitButton.setText("Edit");

            this.addressEdit = address;
        } else {
            this.addressEdit = Optional.empty();
        }
    }

    public void handleSubmit() {
        String validationMessage = validateInput();

        if (!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            return;
        }

        Address address = new Address.Builder(0L)
                .road(roadTextField.getText())
                .houseNumber(houseNumberTextField.getText())
                .city(Objects.equals(cityTextField.getText(), "") ? null : cityTextField.getText())
                .town(Objects.equals(townTextField.getText(), "") ? null : townTextField.getText())
                .village(Objects.equals(villageTextField.getText(), "") ? null : villageTextField.getText())
                .country(countryTextField.getText())
                .longitude(new BigDecimal(longitudeTextField.getText()))
                .latitude(new BigDecimal(latitudeTextField.getText()))
                .build();

        try {
            if (!isNull(addressEdit) && addressEdit.isPresent()) {
                address.setId(addressEdit.get().getId());
                addressRepository.update(address);
            } else {
                addressRepository.save(address);
            }
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while saving address.");
            return;
        }

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    private String validateInput() {
        if (roadTextField.getText().isEmpty()) {
            return "Road field is empty";
        }
        if (houseNumberTextField.getText().isEmpty()) {
            return "House number field is empty";
        }
        if (cityTextField.getText().isEmpty()
                && townTextField.getText().isEmpty()
                && villageTextField.getText().isEmpty()) {
            return "City, town or village field must be filled";
        }
        if (countryTextField.getText().isEmpty()) {
            return "Country field is empty";
        }
        if (!ValidationUtil.isBigDecimal(longitudeTextField.getText())) {
            return "Longitude field is empty";
        }
        if (!ValidationUtil.isBigDecimal(latitudeTextField.getText())) {
            return "Latitude field is empty";
        }
        return "";
    }
}
