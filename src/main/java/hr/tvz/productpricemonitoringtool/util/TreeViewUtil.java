package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Entity;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.List;
import java.util.Optional;

public class TreeViewUtil {

    private TreeViewUtil() {}

    public static <T extends Entity> void treeViewStringConverter(TreeView<T> treeView) {
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    public static void populateCategoryTreeView(TreeView<Category> treeView, CategoryRepository categoryRepository) throws DatabaseConnectionActiveException {
        List<Category> allCategories = categoryRepository.findAllByParentCategoryRecursively(Optional.empty());
        allCategories.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));

        TreeItem<Category> root = new TreeItem<>();
        root.setExpanded(true);

        List<Category> topCategories = allCategories.stream()
                .filter(cat -> cat.getParentCategory().isEmpty())
                .toList();

        for (Category topCategory : topCategories) {
            TreeItem<Category> item = buildTreeItem(topCategory, allCategories);
            root.getChildren().add(item);
        }

        treeView.setRoot(root);
        treeView.setShowRoot(false);
    }

    private static TreeItem<Category> buildTreeItem(Category category, List<Category> allCategories) {
        TreeItem<Category> treeItem = new TreeItem<>(category);
        treeItem.setExpanded(true);

        List<Category> children = allCategories.stream()
                .filter(child -> child.getParentCategory().isPresent()
                        && child.getParentCategory().get().getId().equals(category.getId()))
                .toList();

        for (Category child : children) {
            TreeItem<Category> childItem = buildTreeItem(child, allCategories);
            treeItem.getChildren().add(childItem);
        }
        return treeItem;
    }
}