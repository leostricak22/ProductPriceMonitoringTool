package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.UnsuccessfulHTTPResponseCode;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Coordinates;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Controller for the map picker view.
 * Handles the map picker view.
 * @see Address
 * @see MapUtil
 * @see GeocodeAPI
 * @see AlertDialog
 */
public class MapPickerController {

    @FXML public WebView webView;
    @FXML public TextField pickedLocationTextField;

    private Address address;

    /**
     * Initializes the view.
     * Loads the map and sets the marker on the map if the address is present.
     * @param previousAddress the address to be set on the map
     */
    public void initialize(Optional<Address> previousAddress) {
        WebEngine webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", this);

                if (previousAddress.isPresent()) {
                    address = previousAddress.get();
                    pickedLocationTextField.setText(address.getAddress());

                    webEngine.executeScript("setMarker(" + address.getLongitude() + ", " + address.getLatitude() + ")");
                }
            }
        });

        try {
            File file = new File(Constants.RELATIVE_HTML_PATH + "mapWithPicker.html");
            webEngine.load(file.toURI().toURL().toString());
        } catch (IOException e) {
            AlertDialog.showErrorDialog("Error loading a map");
        }
    }

    /**
     * Handles the pick data button.
     * Closes the view if the address is picked.
     */
    public void handlePickData() {
        if (isNull(address)) {
            AlertDialog.showErrorDialog("No location picked.");
            return;
        }

        Stage stage = (Stage) pickedLocationTextField.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the cancel button.
     * Closes the view.
     */
    public void findLonAndLatOnMap(String data) throws IOException {
        Coordinates coordinates = MapUtil.getCoordinatesFromMap(data);

        try {
            address = GeocodeAPI.fetchAddressFromLonAndLat(coordinates.getLongitude(), coordinates.getLatitude());
            pickedLocationTextField.setText(address.getAddress());
        } catch (UnsuccessfulHTTPResponseCode e) {
            AlertDialog.showErrorDialog("Error fetching address from provided longitude and latitude.");
        } catch (URISyntaxException e) {
            AlertDialog.showErrorDialog("Error creating URI from provided longitude and latitude.");
        }
    }

    public Address getAddress() {
        return address;
    }
}
