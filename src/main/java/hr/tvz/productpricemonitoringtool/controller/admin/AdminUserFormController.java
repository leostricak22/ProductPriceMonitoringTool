package hr.tvz.productpricemonitoringtool.controller.admin;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.UserFileRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Hash;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

import static java.util.Objects.isNull;

public class AdminUserFormController {

    @FXML public TextField nameTextField;
    @FXML public TextField surnameTextField;
    @FXML public TextField emailTextField;
    @FXML public ComboBox<Role> roleComboBox;
    @FXML public PasswordField passwordField;
    @FXML public PasswordField confirmPasswordField;
    @FXML public Button submitButton;

    private Optional<User> user;

    private final UserFileRepository userFileRepository = new UserFileRepository();

    public void initialize(Optional<User> user) {
        roleComboBox.getItems().setAll(Role.values());

        if(user.isPresent()) {
            User userOptional = user.get();
            nameTextField.setText(userOptional.getName());
            surnameTextField.setText(userOptional.getSurname());
            emailTextField.setText(userOptional.getEmail());
            roleComboBox.setValue(userOptional.getRole());

            submitButton.setText("Update");

            this.user = user;
        }
    }

    public void handleSubmit() {
        String validationMessage = validateInput();
        if(!validationMessage.isEmpty()) {
            AlertDialog.showErrorDialog(validationMessage);
            return;
        }

        try {
            if(!isNull(user)) {
                User userOptional = user.get();
                userOptional.setName(nameTextField.getText());
                userOptional.setSurname(surnameTextField.getText());
                userOptional.setEmail(emailTextField.getText());
                userOptional.setRole(roleComboBox.getValue());

                if(!passwordField.getText().isEmpty()) {
                    userOptional.setPassword(Hash.hashPassword(passwordField.getText()));
                }

                userFileRepository.update(userOptional);
            } else {
                if (passwordField.getText().isEmpty()) {
                    AlertDialog.showErrorDialog("Password field must be filled!");
                    return;
                }

                User newUser = new User.Builder(0L, nameTextField.getText())
                        .surname(surnameTextField.getText())
                        .email(emailTextField.getText())
                        .role(roleComboBox.getValue())
                        .password(passwordField.getText())
                        .build();

                userFileRepository.save(newUser);
            }

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            AlertDialog.showErrorDialog("Error occurred while saving user!");
        }
    }

    public String validateInput() {
        if (nameTextField.getText().isEmpty()) {
            return "Name field must be filled!";
        }

        if (surnameTextField.getText().isEmpty()) {
            return "Surname field must be filled!";
        }

        if (emailTextField.getText().isEmpty()) {
            return "Email field must be filled!";
        }

        if (roleComboBox.getValue() == null) {
            return "Role field must be filled!";
        }

        if ((!passwordField.getText().isEmpty() || !confirmPasswordField.getText().isEmpty()) && !passwordField.getText().equals(confirmPasswordField.getText())) {
            return "Password and confirm password fields must match!";
        }

        return "";
    }
}
