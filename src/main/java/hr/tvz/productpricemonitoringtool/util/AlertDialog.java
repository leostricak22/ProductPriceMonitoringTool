package hr.tvz.productpricemonitoringtool.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertDialog {

    private AlertDialog() {}

    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInformationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        alert.showAndWait();
    }
}