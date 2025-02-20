package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.model.AuditLog;
import hr.tvz.productpricemonitoringtool.model.AuditLogManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class AuditLogController {

    @FXML
    public TableView<AuditLog<?, ?>> auditLogTableView;

    @FXML
    public TableColumn<AuditLog<?, ?>, String> fieldTableColumn;

    @FXML
    public TableColumn<AuditLog<?, ?>, String> oldValueTableColumn;

    @FXML
    public TableColumn<AuditLog<?, ?>, String> newValueTableColumn;

    @FXML
    public TableColumn<AuditLog<?, ?>, String> userTableColumn;

    @FXML
    public TableColumn<AuditLog<?, ?>, String> dateTableColumn;

    @FXML
    public void initialize() {
        fieldTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getField()));

        oldValueTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOldValue().toString()));

        newValueTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNewValue().toString()));

        userTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getName()));

        dateTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"))));

        ObservableList<AuditLog<?, ?>> logs = FXCollections.observableArrayList(AuditLogManager.getLogs());
        auditLogTableView.setItems(logs);
    }
}