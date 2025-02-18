package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ClipboardUtil;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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

        Set<User> users = companyRepository.findAllUsers(selectedCompany);
        staffTableView.setItems(FXCollections.observableArrayList(users));
    }

    public void handleShowCode() {
        if (codeLabel.isVisible()) {
            codeLabel.setVisible(false);
            copyCodeLabel.setVisible(false);

            copyCodeLabel.setText("(Click the code to copy)");
            showCodeButton.setText("Show code");
            return;
        }

        if (Session.getSelectedCompany().isEmpty()) {
            AlertDialog.showErrorDialog("Please select a company first.");
            return;
        }

        codeLabel.setText(Session.getSelectedCompany().get().getJoinCode());
        codeLabel.setVisible(true);
        copyCodeLabel.setVisible(true);
        showCodeButton.setText("Hide code");
    }

    public void handleCopyCode() {
        if (!codeLabel.isVisible()) {
            return;
        }

        ClipboardUtil.copyToClipboard(codeLabel.getText());
        copyCodeLabel.setText("(Copied to clipboard)");
    }
}