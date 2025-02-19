package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.controller.SearchController;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserFileRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class AdminUsersController implements SearchController {

    @FXML public TextField idTextField;
    @FXML public TextField nameTextField;
    @FXML public TextField surnameTextField;
    @FXML public TextField emailTextField;
    @FXML public TextField roleTextField;

    @FXML public TableView<User> userTableView;
    @FXML public TableColumn<User, Long> idTableColumn;
    @FXML public TableColumn<User, String> nameTableColumn;
    @FXML public TableColumn<User, String> surnameTableColumn;
    @FXML public TableColumn<User, String> emailTableColumn;
    @FXML public TableColumn<User, String> roleTableColumn;

    @FXML public Label removeFiltersLabel;

    private final UserFileRepository userRepository = new UserFileRepository();

    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        nameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        surnameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSurname()));

        emailTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));

        roleTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().toString()));

        removeFiltersLabel.setVisible(false);
        filter();
    }

    @Override
    public void filter() {
        List<User> users = new ArrayList<>(userRepository.findAll());
        users.sort((user1, user2) -> user1.getId().compareTo(user2.getId()));

        String idValue = this.idTextField.getText();
        String nameValue = this.nameTextField.getText();
        String surnameValue = this.surnameTextField.getText();
        String emailValue = this.emailTextField.getText();
        String roleValue = this.roleTextField.getText();

        showFilterLabel();

        userTableView.setItems(FXCollections.observableArrayList(users.stream()
                .filter(user -> idValue.isEmpty() || user.getId().toString().equals(idValue))
                .filter(user -> nameValue.isEmpty() || user.getName().contains(nameValue))
                .filter(user -> surnameValue.isEmpty() || user.getSurname().contains(surnameValue))
                .filter(user -> emailValue.isEmpty() ||
                        (!isNull(user.getEmail()) &&
                                user.getEmail().toLowerCase().contains(emailValue.toLowerCase())))
                .filter(user -> roleValue.isEmpty() || user.getRole().toString().equals(roleValue))
                .toList()));
    }

    @Override
    public void removeFilters() {
        idTextField.clear();
        nameTextField.clear();
        surnameTextField.clear();
        emailTextField.clear();
        roleTextField.clear();

        filter();
    }

    @Override
    public void handleAddNewButtonClick() {
        SceneLoader.loadUsersPopupScene("admin_user_form", "Add new user", Optional.empty());
        filter();
    }

    @Override
    public void handleEditButtonClick() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if(isNull(selectedUser)) {
            AlertDialog.showErrorDialog("Please select a user to edit.");
            return;
        }

        SceneLoader.loadUsersPopupScene("admin_user_form", "Edit user", Optional.of(selectedUser));
        filter();
    }

    @Override
    public void handleDeleteButtonClick() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (isNull(selectedUser)) {
            AlertDialog.showErrorDialog("Please select a company to delete.");
            return;
        }

        Optional<ButtonType> result = AlertDialog.showConfirmationDialog(
                "Are you sure you want to delete the selected product?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userRepository.delete(selectedUser);
        }

        filter();
    }

    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
                !idTextField.getText().isEmpty() ||
                !nameTextField.getText().isEmpty() ||
                !surnameTextField.getText().isEmpty() ||
                !emailTextField.getText().isEmpty() ||
                !roleTextField.getText().isEmpty());
    }
}