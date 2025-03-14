package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.Coordinates;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller for the map radius view.
 * Handles the map radius view.
 * @see Company
 * @see CompanyRepository
 * @see MapUtil
 * @see AlertDialog
 */
public class MapRadiusController {

    @FXML public WebView webView;
    @FXML public Slider radiusSlider;
    @FXML public TextField radiusTextField;

    private final CompanyRepository companyRepository = new CompanyRepository();

    Set<Company> companiesInRadius = null;

    /**
     * Initializes the view.
     * Loads the map and sets the radius on the map.
     */
    public void initialize() {
        WebEngine webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", this);
            }
        });

        try {
            File file = new File(Constants.RELATIVE_HTML_PATH + "mapWithRadius.html");
            webEngine.load(file.toURI().toURL().toString());
        } catch (IOException e) {
            AlertDialog.showErrorDialog("Error loading a map");
            return;
        }

        radiusSlider.valueProperty().addListener((observable, oldValue, newValue) -> handleSliderDragDone(newValue));
    }

    /**
     * Handles the form submit.
     * Fetches the data from the map and calculates the companies in the radius.
     */
    public void onFetchData() {
        WebEngine webEngine = webView.getEngine();
        String data = (String) webEngine.executeScript("sendData()");

        findLonAndLatOnMap(data);

        Stage stage = (Stage) radiusTextField.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the slider drag done event.
     * Updates the radius text field and the map radius.
     */
    public void handleSliderDragDone(Number newValue) {
        WebEngine webEngine = webView.getEngine();
        if (!radiusTextField.isFocused()) {
            radiusTextField.setText(String.valueOf(newValue));
        }
        webEngine.executeScript("updateRadius(" + newValue + ")");
    }

    /**
     * Handles the text view radius update event.
     * Updates the radius slider and the map radius.
     */
    public void handleTextViewRadiusUpdate() {
        WebEngine webEngine = webView.getEngine();

        String radius = radiusTextField.getText();
        if (radius.isEmpty() || !ValidationUtil.isPositiveBigDecimal(radius)) {
            radius = "0";
        }

        radiusSlider.setValue(new BigDecimal(radius).doubleValue());

        webEngine.executeScript("updateRadius(" + radius + ")");
    }

    /**
     * Fetches the data from the map and calculates the companies in the radius.
     * @param data The data from the map.
     */
    public void findLonAndLatOnMap(String data) {
        Coordinates coordinates = MapUtil.getCoordinatesFromMap(data);

        Set<Company> companies;
        try {
            companies = companyRepository.findAll();
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error fetching companies from database.");
            return;
        }

        if (!ValidationUtil.isPositiveBigDecimal(radiusTextField.getText())) {
            AlertDialog.showErrorDialog("Radius must be a positive number.");
            return;
        }

        BigDecimal radius = new BigDecimal(radiusTextField.getText());
        Set<Company> companiesInRadiusSet = new HashSet<>();
        for (Company company : companies) {
            Coordinates companyCoordinates = new Coordinates.Builder()
                    .latitude(company.getAddress().getLatitude())
                    .longitude(company.getAddress().getLongitude())
                    .build();

            BigDecimal distance = MapUtil.calculateDistance(coordinates, companyCoordinates);
            if (distance.compareTo(radius) <= 0) {
                companiesInRadiusSet.add(company);
            }
        }

        this.companiesInRadius = companiesInRadiusSet;
    }

    /**
     * Returns the companies in the radius.
     */
    public Set<Company> getCompaniesInRadius() {
        return companiesInRadius;
    }
}