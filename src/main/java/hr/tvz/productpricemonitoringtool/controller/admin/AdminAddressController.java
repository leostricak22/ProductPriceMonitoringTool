package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.SearchController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.PopupSceneLoader;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Controller for the admin address view.
 * Handles the address search and filtering.
 * @see Address
 * @see AddressRepository
 * @see SearchController
 * @see AlertDialog
 * @see PopupSceneLoader
 */
public class AdminAddressController implements SearchController {

    @FXML public TextField idTextField;
    @FXML public TextField roadTextField;
    @FXML public TextField houseNumberTextField;
    @FXML public TextField cityTextField;
    @FXML public TextField townTextField;
    @FXML public TextField villageTextField;
    @FXML public TextField countryTextField;

    @FXML public TableView<Address> addressTableView;
    @FXML public TableColumn<Address, Long> idTableColumn;
    @FXML public TableColumn<Address, String> roadTableColumn;
    @FXML public TableColumn<Address, String> houseNumberTableColumn;
    @FXML public TableColumn<Address, String> cityTableColumn;
    @FXML public TableColumn<Address, String> townTableColumn;
    @FXML public TableColumn<Address, String> villageTableColumn;
    @FXML public TableColumn<Address, String> countryTableColumn;

    @FXML public Label removeFiltersLabel;

    private final AddressRepository addressRepository = new AddressRepository();

    /**
     * Initializes the view.
     */
    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        roadTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoad().equals("?") ? "-" : cellData.getValue().getRoad()));

        houseNumberTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHouseNumber().equals("?") ? "-" : cellData.getValue().getHouseNumber()));

        cityTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        isNull(cellData.getValue().getCity()) || cellData.getValue().getCity().equals("?")
                                ? "-" : cellData.getValue().getCity()));

        townTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        isNull(cellData.getValue().getTown()) || cellData.getValue().getTown().equals("?")
                                ? "-" : cellData.getValue().getTown()));

        villageTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        isNull(cellData.getValue().getVillage()) || cellData.getValue().getVillage().equals("?")
                                ? "-" : cellData.getValue().getVillage()));


        countryTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCountry().equals("?") ? "-" : cellData.getValue().getCountry()));

        removeFiltersLabel.setVisible(false);
        filter();
    }

    /**
     * Filters the addresses based on the input values.
     */
    @Override
    public void filter() {
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = new ArrayList<>(addressRepository.findAll());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading addresses.");
            return;
        }

        addresses.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));

        String idValue = this.idTextField.getText();
        String roadValue = this.roadTextField.getText();
        String houseNumberValue = this.houseNumberTextField.getText();
        String cityValue = this.cityTextField.getText();
        String townValue = this.townTextField.getText();
        String villageValue = this.villageTextField.getText();
        String countryValue = this.countryTextField.getText();

        showFilterLabel();

        addressTableView.setItems(FXCollections.observableArrayList(addresses.stream()
                .filter(address -> idValue.isEmpty() || address.getId().toString().equals(idValue))
                .filter(address -> roadValue.isEmpty() || address.getRoad().contains(roadValue))
                .filter(address -> houseNumberValue.isEmpty() || address.getHouseNumber().contains(houseNumberValue))
                .filter(address -> cityValue.isEmpty() || (!isNull(address.getCity()) && address.getCity().contains(cityValue)))
                .filter(address -> townValue.isEmpty() || (!isNull(address.getTown()) && address.getTown().contains(townValue)))
                .filter(address -> villageValue.isEmpty() || (!isNull(address.getVillage()) && address.getVillage().contains(villageValue)))
                .filter(address -> countryValue.isEmpty() || address.getCountry().contains(countryValue))
                .toList()));
    }

    /**
     * Removes the filters and refreshes the table.
     */
    @Override
    public void removeFilters() {
        idTextField.clear();
        roadTextField.clear();
        houseNumberTextField.clear();
        cityTextField.clear();
        townTextField.clear();
        villageTextField.clear();
        countryTextField.clear();

        filter();
    }

    /**
     * Handles the add new button click event.
     * Opens the address form popup.
     */
    @Override
    public void handleAddNewButtonClick() {
        PopupSceneLoader.loadAddressPopupScene("admin_address_form", "Add new address", Optional.empty());
        filter();
    }

    /**
     * Handles the edit button click event.
     * Opens the address form popup with the selected address.
     * Shows an error dialog if no address is selected.
     */
    @Override
    public void handleEditButtonClick() {
        Address selectedAddress = addressTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedAddress)) {
            AlertDialog.showErrorDialog("Please select an address to edit.");
            return;
        }

        PopupSceneLoader.loadAddressPopupScene("admin_address_form", "Edit address", Optional.of(selectedAddress));
        filter();
    }

    /**
     * Handles the delete button click event.
     * Deletes the selected address.
     * Shows a confirmation dialog before deleting.
     * Shows an error dialog if no address is selected.
     */
    @Override
    public void handleDeleteButtonClick() {
        Address selectedAddress = addressTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedAddress)) {
            AlertDialog.showErrorDialog("Please select an address to delete.");
            return;
        }

        Optional<ButtonType> result = AlertDialog.showConfirmationDialog(
                "Are you sure you want to delete the selected product?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                addressRepository.delete(selectedAddress);
            } catch (DatabaseConnectionActiveException e) {
                AlertDialog.showErrorDialog("Error while deleting a company.");
            }
        }

        filter();
    }

    /**
     * Shows the remove filters label if any filter is applied.
     * Hides the label if no filter is applied.
     */
    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
            !idTextField.getText().isEmpty() ||
            !roadTextField.getText().isEmpty() ||
            !houseNumberTextField.getText().isEmpty() ||
            !cityTextField.getText().isEmpty() ||
            !townTextField.getText().isEmpty() ||
            !villageTextField.getText().isEmpty() ||
            !countryTextField.getText().isEmpty());
    }
}