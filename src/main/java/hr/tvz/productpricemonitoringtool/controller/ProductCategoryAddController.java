package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.TreeViewUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ProductCategoryAddController {

    @FXML public TreeView<Category> categoryTreeView;
    @FXML public TextField newCategoryNameTextField;
    @FXML public Label categoryHierarchyLabel;
    @FXML public Label resetCategoryHierarchyLabel;

    private final CategoryRepository categoryRepository = new CategoryRepository();
    private String categoryHierarchy = "";

    public void initialize() {
        TreeViewUtil.treeViewStringConverter(categoryTreeView);

        categoryTreeView.setRoot(new TreeItem<>());
        categoryTreeView.getRoot().setExpanded(true);

        try {
            TreeViewUtil.populateCategoryTreeView(categoryTreeView, categoryRepository);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE, Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
        }

        categoryTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> {
                    try {
                        handleTreeItemSelection(newItem);
                    } catch (DatabaseConnectionActiveException e) {
                        AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE,
                                Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
                    }
                }
        );
    }

    private void handleTreeItemSelection(TreeItem<Category> newItem) throws DatabaseConnectionActiveException {
        if (newItem == null) {
            return;
        }

        categoryHierarchy = categoryRepository.findCategoryHierarchy(newItem.getValue().getId());
        categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX + categoryHierarchy);

        if (!newCategoryNameTextField.getText().isBlank()) {
            categoryHierarchyLabel.setText(categoryHierarchyLabel.getText() + " -> " + newCategoryNameTextField.getText());
        }
    }

    public void handleNewCategoryNameChange() {
        String textFieldValue = newCategoryNameTextField.getText();

        if (textFieldValue.isEmpty()) {
            resetCategoryHierarchyLabel.setVisible(false);
            categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX + categoryHierarchy);
            return;
        }

        resetCategoryHierarchyLabel.setVisible(true);
        categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX + categoryHierarchy);

        if (!categoryHierarchy.isEmpty()) {
            categoryHierarchyLabel.setText(categoryHierarchyLabel.getText() + " -> ");
        }

        categoryHierarchyLabel.setText(categoryHierarchyLabel.getText() + textFieldValue);
    }

    public void handleResetCategoryHierarchy() {
        newCategoryNameTextField.clear();
        resetCategoryHierarchyLabel.setVisible(false);
        categoryHierarchy = "";
        categoryHierarchyLabel.setText(Constants.CATEGORY_HIERARCHY_PREFIX);
    }
}
