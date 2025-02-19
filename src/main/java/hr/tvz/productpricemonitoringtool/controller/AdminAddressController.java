package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

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

    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        roadTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoad()));

        houseNumberTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHouseNumber()));

        cityTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        isNull(cellData.getValue().getCity()) ? "-" : cellData.getValue().getCity()));

        townTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        isNull(cellData.getValue().getTown()) ? "-" : cellData.getValue().getTown()));

        villageTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        isNull(cellData.getValue().getVillage()) ? "-" : cellData.getValue().getVillage()));

        countryTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCountry()));

        removeFiltersLabel.setVisible(false);
        filter();
    }

    @Override
    public void filter() {
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = new ArrayList<>(addressRepository.findAll());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading addresses.");
            return;
        }

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