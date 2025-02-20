package hr.tvz.productpricemonitoringtool.model;

import java.io.*;
import java.time.LocalDateTime;

public class AuditLog<T, U> implements Serializable {

    private final String field;
    private final T oldValue;
    private final U newValue;
    private final User user;
    private final LocalDateTime timestamp;

    public AuditLog(String field, T oldValue, U newValue, User user) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
        this.timestamp = LocalDateTime.now();
    }

    public String getField() { return field; }
    public T getOldValue() { return oldValue; }
    public U getNewValue() { return newValue; }
    public User getUser() { return user; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + user.getName() + " changed " + field + " from '"
                + oldValue + "' to '" + newValue + "'";
    }
}