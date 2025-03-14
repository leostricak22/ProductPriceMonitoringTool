package hr.tvz.productpricemonitoringtool.model;

import java.io.*;
import java.time.LocalDateTime;

/**
 * Audit log model.
 * Represents the audit log in the model.
 * @param <T> the type of the old value
 * @param <U> the type of the new value
 */
public class AuditLog<T, U> implements Serializable {

    private final String field;
    private final transient T oldValue;
    private final transient U newValue;
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