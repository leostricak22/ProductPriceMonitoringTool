package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.JavaBridge;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;

public class MapPickerController {

    @FXML public WebView webView;
    @FXML public TextField pickedLocationTextField;

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
            System.out.println(Constants.RELATIVE_HTML_PATH + "mapWithPicker.html");
            File file = new File(Constants.RELATIVE_HTML_PATH + "mapWithPicker.html");
            webEngine.load(file.toURI().toURL().toString());
        } catch (IOException e) {
            AlertDialog.showErrorDialog("Error occurred", "Error loading a map");
        }
    }

    public void handlePickData() {
        // it'll be implemented in the future
    }

}
