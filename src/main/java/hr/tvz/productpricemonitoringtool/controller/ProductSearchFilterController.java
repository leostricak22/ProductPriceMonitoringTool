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

public class ProductSearchFilterController {

    @FXML public TextField priceFromTextField;
    @FXML public TextField priceToTextField;
    @FXML public Label radiusLabel;

    private FilterSearch filterSearch = new FilterSearch();

    public void initialize() {
        radiusLabel.setText("");
    }

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

    public String validateInput() {
        if (!ValidationUtil.isPositiveBigDecimal(priceFromTextField.getText()) && !priceFromTextField.getText().isEmpty()) {
            return "Price From field must be a positive number.";
        }

        if (!ValidationUtil.isPositiveBigDecimal(priceToTextField.getText()) && !priceToTextField.getText().isEmpty()) {
            return "Price To field must be a positive number.";
        }

        return "";
    }

    public FilterSearch getFilterSearch() {
        return filterSearch;
    }
}