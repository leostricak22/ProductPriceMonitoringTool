package hr.tvz.productpricemonitoringtool.thread;

import hr.tvz.productpricemonitoringtool.controller.TopBarController;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.PriceNotification;
import hr.tvz.productpricemonitoringtool.model.StaffNotification;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * CheckNotificationsThread class.
 * Thread class for checking notifications.
 * Contains methods for starting and stopping the thread.
 */
public class CheckNotificationsThread {

    private static Timeline timeline;

    private CheckNotificationsThread() {}

    /**
     * Start the thread.
     */
    public static void start() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            Task<Void> checkNotificationsTask = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        PriceNotification priceNotification = new PriceNotification();

                        priceNotification.checkPriceChange();
                        if (!PriceNotification.getNewCompanyProductRecords().isEmpty()) {
                            Platform.runLater(() -> TopBarController.getInstance().changeNotificationBellIcon());
                        }

                        StaffNotification staffNotification = new StaffNotification();
                        staffNotification.checkStaffChange();
                        if (!StaffNotification.getNewUserCompanyDBORecords().isEmpty()) {
                            Platform.runLater(() -> TopBarController.getInstance().changeNotificationBellIcon());
                        }
                    } catch (DatabaseConnectionActiveException e) {
                        Platform.runLater(() -> AlertDialog.showErrorDialog("Error while checking price change."));
                    }
                    return null;
                }
            };

            new Thread(checkNotificationsTask).start();
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Stop the thread.
     */
    public static void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}