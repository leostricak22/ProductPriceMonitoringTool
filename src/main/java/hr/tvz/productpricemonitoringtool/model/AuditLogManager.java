package hr.tvz.productpricemonitoringtool.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogManager {

    private static final String FILE_PATH = "files/dat/audit_log.dat";

    private static final List<AuditLog<?, ?>> logs = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(AuditLogManager.class);

    private AuditLogManager() {}

    public static <T, U> void logChange(String field, T oldValue, U newValue, User user) {
        AuditLog<T, U> entry = new AuditLog<>(field, oldValue, newValue, user);
        logs.add(entry);
        save();
    }

    private static void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(logs);
        } catch (IOException e) {
            log.error("Error saving logs: {}", e.getMessage());
        }
    }

    public static void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            List<AuditLog<?, ?>> loadedLogs = (List<AuditLog<?, ?>>) ois.readObject();
            logs.addAll(loadedLogs);
        } catch (IOException | ClassNotFoundException e) {
            log.warn("No logs found, starting with an empty list.");
        }
    }

    public static List<AuditLog<?, ?>> getLogs() {
        return logs;
    }
}