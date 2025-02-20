package hr.tvz.productpricemonitoringtool.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Audit log manager.
 * Manages the audit logs in the model.
 */
public class AuditLogManager {

    private static final String FILE_PATH = "files/dat/audit_log.dat";

    private static final List<AuditLog<?, ?>> logs = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(AuditLogManager.class);

    private AuditLogManager() {}

    /**
     * Logs a change.
     * @param field the field that was changed
     * @param oldValue the old value
     * @param newValue the new value
     * @param user the user that made the change
     * @param <T> the type of the old value
     * @param <U> the type of the new value
     */
    public static <T, U> void logChange(String field, T oldValue, U newValue, User user) {
        AuditLog<T, U> entry = new AuditLog<>(field, oldValue, newValue, user);
        logs.add(entry);
        save();
    }

    /**
     * Saves the logs to a file.
     * If an error occurs, logs the error.
     * If the file does not exist, creates a new file.
     * If the file exists, overwrites the file.
     * If the file is not writable, logs an error.
     */
    private static void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(logs);
        } catch (IOException e) {
            log.error("Error saving logs: {}", e.getMessage());
        }
    }

    /**
     * Loads the logs from a file.
     * If an error occurs, logs the error.
     * If the file does not exist, logs a warning.
     */
    public static void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            List<AuditLog<?, ?>> loadedLogs = (List<AuditLog<?, ?>>) ois.readObject();
            logs.addAll(loadedLogs);
        } catch (IOException | ClassNotFoundException e) {
            log.warn("No logs found, starting with an empty list.");
        }
    }

    /**
     * Gets the logs.
     * @return the logs
     */
    public static List<AuditLog<?, ?>> getLogs() {
        return logs;
    }
}