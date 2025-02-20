package hr.tvz.productpricemonitoringtool.model;

/**
 * Notification model.
 * Represents the notification in the model.
 * Abstract class for all notifications.
 * Contains save method.
 */
public sealed interface Notification permits StaffNotification, PriceNotification  {

    public abstract void save();
}
