package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.HashSet;
import java.util.Set;

public class CompanyStaffController {

    @FXML public Button showCodeButton;
    @FXML public Label codeLabel;
    @FXML public Label copyCodeLabel;

    @FXML public TableView<User> staffTableView;
    @FXML public TableColumn<User, Long> staffIdTableColumn;
    @FXML public TableColumn<User, String> staffNameTableColumn;
    @FXML public TableColumn<User, String> staffSurnameTableColumn;
    @FXML public TableColumn<User, String> staffEmailTableColumn;
    @FXML public TableColumn<User, String> staffRoleTableColumn;

    private final CompanyRepository companyRepository = new CompanyRepository();

    public void initialize() {
        codeLabel.setVisible(false);
        copyCodeLabel.setVisible(false);

        staffIdTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        staffNameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        staffSurnameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        staffEmailTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));

        staffRoleTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().toString()));

        Company selectedCompany = null;
        if (Session.getSelectedCompany().isPresent()) {
            selectedCompany = Session.getSelectedCompany().get();
        }

        Set<User> users = new HashSet<>();
        try {
            users = companyRepository.findAllUsers(selectedCompany);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Database connection is active. Please try again later.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        staffTableView.setItems(FXCollections.observableArrayList(users));
    }

    public void handleShowCode() {
        if (codeLabel.isVisible()) {
            codeLabel.setVisible(false);
            copyCodeLabel.setVisible(false);

            showCodeButton.setText("Show code");
            return;
        }

        codeLabel.setVisible(true);
        copyCodeLabel.setVisible(true);
        showCodeButton.setText("Hide code");
    }
}