package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.JavaBridge;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;

public class MapRadiusController {

    @FXML public WebView webView;
    @FXML public Slider radiusSlider;
    @FXML public TextField radiusTextField;

    public void initialize() {
        WebEngine webEngine = webView.getEngine();

        JavaBridge javaBridge = new JavaBridge();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", javaBridge);
            }
        });

        try {
            File file = new File(Constants.RELATIVE_HTML_PATH + "mapWithRadius.html");
            webEngine.load(file.toURI().toURL().toString());
        } catch (IOException e) {
            AlertDialog.showErrorDialog("Error occurred", "Error loading a map");
            return;
        }

        radiusSlider.valueProperty().addListener((observable, oldValue, newValue) -> handleSliderDragDone(newValue));

    }

    public void onFetchData() {
        WebEngine webEngine = webView.getEngine();
        String data = (String) webEngine.executeScript("sendData()");
        showAlert(data);
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data from HTML");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleSliderDragDone(Number newValue) {
        WebEngine webEngine = webView.getEngine();
        radiusTextField.setText(String.valueOf(newValue));
        webEngine.executeScript("updateRadius(" + newValue + ")");
    }

    public void handleTextViewRadiusUpdate() {
        WebEngine webEngine = webView.getEngine();

        String radius = radiusTextField.getText();
        if (radius.isEmpty() || !radius.matches("\\d+")) {
            radius = "0";
        }

        radiusSlider.setValue(Long.parseLong(radius));

        webEngine.executeScript("updateRadius(" + radius + ")");
    }
}