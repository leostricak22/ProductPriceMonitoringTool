package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.UnsuccessfulHTTPResponseCode;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import static java.util.Objects.isNull;

public class MapPickerController {

    @FXML public WebView webView;
    @FXML public TextField pickedLocationTextField;

    private Address address;

    public void initialize() {
        WebEngine webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", this);
            }
        });

        try {
            File file = new File(Constants.RELATIVE_HTML_PATH + "mapWithPicker.html");
            webEngine.load(file.toURI().toURL().toString());
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE, "Error loading a map");
        }
    }

    public void handlePickData() {
        if (isNull(address)) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE, "No location picked.");
            return;
        }

        Stage stage = (Stage) pickedLocationTextField.getScene().getWindow();
        stage.close();
    }

    public void findLonAndLatOnMap(String data) throws IOException {
        String[] dataArray = data.split(" ");
        String latitudeString = dataArray[0];
        String longitudeString = dataArray[1];

        if (isNull(longitudeString) || isNull(latitudeString)) {
            throw new IllegalArgumentException("Longitude and latitude must be provided.");
        }
        if (!ValidationUtil.isBigDecimal(longitudeString) || !ValidationUtil.isBigDecimal(latitudeString)) {
            throw new IllegalArgumentException("Longitude and latitude must be positive numbers.");
        }

        BigDecimal longitude = new BigDecimal(longitudeString);
        BigDecimal latitude = new BigDecimal(latitudeString);

        try {
            address = GeocodeAPI.fetchAddressFromLonAndLat(longitude, latitude);
            pickedLocationTextField.setText(address.getAddress());
        } catch (UnsuccessfulHTTPResponseCode e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                    "Error fetching address from provided longitude and latitude.");
        }
    }

    public Address getAddress() {
        return address;
    }
}
