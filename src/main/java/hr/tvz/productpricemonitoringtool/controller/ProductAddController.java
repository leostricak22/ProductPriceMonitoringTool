package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.TreeViewUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;

public class ProductAddController {

    @FXML public GridPane mainPane;
    @FXML public TextField nameLabel;

    @FXML public TreeView<Category> categoryTreeView;

    private final CategoryRepository categoryRepository = new CategoryRepository();

    public void initialize() {
        TreeViewUtil.treeViewStringConverter(categoryTreeView);

        categoryTreeView.setRoot(new TreeItem<>());
        categoryTreeView.getRoot().setExpanded(true);

        try {
            TreeViewUtil.populateCategoryTreeView(categoryTreeView, categoryRepository);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog(Constants.ALERT_ERROR_TITLE, Constants.DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE);
        }
    }

    public void handleImagePick() {
    }

    public void handleAddProduct() {
    }

    public void handleSelectCategory() {
    }
}
