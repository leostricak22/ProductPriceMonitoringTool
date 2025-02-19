package hr.tvz.productpricemonitoringtool.model;

public abstract sealed class Notification permits StaffNotification, PriceNotification  {

    public abstract void save();
}
