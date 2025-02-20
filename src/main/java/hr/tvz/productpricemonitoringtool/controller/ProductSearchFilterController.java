package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.*;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller for the product search filter view.
 * Handles the form for filtering products.
 * Validates the input and sets the filter search object.
 * @see FilterSearch
 * @see AlertDialog
 * @see SceneLoader
 */
public class ProductSearchFilterController {

    @FXML public TextField priceFromTextField;
    @FXML public TextField priceToTextField;
    @FXML public Label radiusLabel;

    private FilterSearch filterSearch = new FilterSearch();

    /**
     * Initializes the view.
     */
    public void initialize() {
        radiusLabel.setText("");
    }

    /**
     * Handles the location radius button.
     * Opens the location radius popup.
     */
    public void handleLocationRadius() {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene("map_radius", "Location Radius");
        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog("Failed to load location radius scene.");
            return;
        }

        MapRadiusController controller = loader.get().getController();
        filterSearch.setCompaniesInRadius(Optional.of(controller.getCompaniesInRadius()));

        if (filterSearch.getCompaniesInRadius().isEmpty()) {
            radiusLabel.setText("");
            return;
        }

        radiusLabel.setText("Companies in radius: " + filterSearch.getCompaniesInRadius().get().size());
    }

    /**
     * Handles the apply filter button.
     * Validates the input and sets the filter search object.
     * Closes the popup.
     */
    public void handleApplyFilter() {
        String validationMessage = validateInput();

        if (!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            return;
        }

        filterSearch.setPriceFromValue(priceFromTextField.getText().isEmpty() ?
                Optional.empty() : Optional.of(new BigDecimal(priceFromTextField.getText())));
        filterSearch.setPriceToValue(priceToTextField.getText().isEmpty() ?
                Optional.empty() : Optional.of(new BigDecimal(priceToTextField.getText())));

        Stage stage = (Stage) priceFromTextField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates the input.
     * @return the validation message
     */
    public String validateInput() {
        if (!ValidationUtil.isPositiveBigDecimal(priceFromTextField.getText()) && !priceFromTextField.getText().isEmpty()) {
            return "Price From field must be a positive number.";
        }

        if (!ValidationUtil.isPositiveBigDecimal(priceToTextField.getText()) && !priceToTextField.getText().isEmpty()) {
            return "Price To field must be a positive number.";
        }

        return "";
    }

    /**
     * Gets the filter search object.
     * @return the filter search object
     */
    public FilterSearch getFilterSearch() {
        return filterSearch;
    }
}