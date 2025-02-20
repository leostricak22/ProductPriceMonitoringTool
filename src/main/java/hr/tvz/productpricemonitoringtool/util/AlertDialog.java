package hr.tvz.productpricemonitoringtool.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * AlertDialog class.
 * Contains methods for showing error, information and confirmation dialogs.
 */
public class AlertDialog {

    private AlertDialog() {}

    /**
     * Show error dialog.
     * @param message Error message.
     *                Message to be shown in the dialog.
     */
    public static void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Constants.ALERT_ERROR_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show information dialog.
     * @param title Dialog title.
     *              Title of the dialog.
     * @param message Information message.
     *                Message to be shown in the dialog.
     */
    public static void showInformationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        alert.showAndWait();
    }

    /**
     * Show confirmation dialog.
     * @param message Confirmation message.
     *                Message to be shown in the dialog.
     * @return Optional ButtonType.
     *         Button type of the dialog.
     */
    public static Optional<ButtonType> showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}