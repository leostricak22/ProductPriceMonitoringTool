package hr.tvz.productpricemonitoringtool.model;

import java.io.Serializable;

public non-sealed class StaffNotification extends Notification  implements Serializable {

    private final String message;

    public StaffNotification(String message) {
        this.message = message;
    }

    @Override
    public void save() {
        System.out.println("Staff Notification: " + message);
    }
}