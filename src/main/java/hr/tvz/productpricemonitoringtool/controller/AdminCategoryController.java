package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.ComboBoxUtil;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class AdminCategoryController implements SearchController {

    @FXML public TextField idTextField;
    @FXML public TextField nameTextField;
    @FXML public ComboBox<Category> parentCategoryComboBox;

    @FXML public TableView<Category> categoryTableView;
    @FXML public TableColumn<Category, Long> idTableColumn;
    @FXML public TableColumn<Category, String> nameTableColumn;
    @FXML public TableColumn<Category, String> parentCategoryTableColumn;

    @FXML public Label removeFiltersLabel;

    private final CategoryRepository categoryRepository = new CategoryRepository();

    @Override
    public void initialize() {
        idTableColumn.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        nameTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));


        parentCategoryTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getParentCategory().isEmpty() ? "-"
                                : cellData.getValue().getParentCategory().get().getName()));

        ComboBoxUtil.comboBoxStringConverter(parentCategoryComboBox);
        try {
            parentCategoryComboBox.setItems(FXCollections.observableArrayList(categoryRepository.findAll()));
        } catch (Exception e) {
            AlertDialog.showErrorDialog("Error while loading categories.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        removeFiltersLabel.setVisible(false);
        filter();
    }

    @Override
    public void filter() {
        List<Category> categories = new ArrayList<>();
        try {
            categories = new ArrayList<>(categoryRepository.findAll());
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while loading categories.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        String idValue = this.idTextField.getText();
        String nameValue = this.nameTextField.getText();
        Category parentCategoryValue = this.parentCategoryComboBox.getValue();

        showFilterLabel();

        categoryTableView.setItems(FXCollections.observableArrayList(categories.stream()
                .filter(category -> idValue.isEmpty() || category.getId().toString().equals(idValue))
                .filter(category -> nameValue.isEmpty() || category.getName().contains(nameValue))
                .filter(category -> isNull(parentCategoryValue) ||
                        category.getParentCategory().isPresent() &&
                                category.getParentCategory().get().equals(parentCategoryValue))
                .toList()));
    }

    @Override
    public void removeFilters() {
        idTextField.clear();
        nameTextField.clear();
        parentCategoryComboBox.setValue(null);

        filter();
    }

    private void showFilterLabel() {
        removeFiltersLabel.setVisible(
                !idTextField.getText().isEmpty() ||
                        !nameTextField.getText().isEmpty() ||
                        parentCategoryComboBox.getValue() != null);
    }
}