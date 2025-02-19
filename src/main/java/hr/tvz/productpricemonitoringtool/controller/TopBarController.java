package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.FilterSearch;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Optional;

public class TopBarController {

    @FXML public Circle userProfilePictureCircle;
    @FXML public Rectangle notificationIconRectangle;
    @FXML public TextField searchProductsTextField;
    @FXML public Button filterButton;

    private Boolean filterApplied = false;
    private FilterSearch filterSearch = new FilterSearch();

    public void initialize() {
        Image image = Session.getUserProfilePicture();
        userProfilePictureCircle.setFill(new ImagePattern(image));

        notificationIconRectangle.setFill(new ImagePattern(
                new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/notifications.png")));
    }

    public void handleUserEditRedirect() {
        SceneLoader.loadScene("user_edit", "Edit User");
    }

    public void handleFilter() {
        Optional<FXMLLoader> loader = SceneLoader.loadPopupScene("product_search_filter", "Filter");
        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog("Failed to fetch filtered items.");
            return;
        }

        ProductSearchFilterController controller = loader.get().getController();
        filterSearch = controller.getFilterSearch();

        if (filterSearch.getPriceFromValue().isPresent() ||
                filterSearch.getPriceToValue().isPresent() ||
                filterSearch.getCompaniesInRadius().isPresent()) {
            filterButton.setText("Applied");
            filterApplied = true;
        }
    }

    public void handleSearch() {
        filterSearch.setProductName(searchProductsTextField.getText().isEmpty() ?
                Optional.empty() : Optional.of(searchProductsTextField.getText()));
        if (filterSearch.getProductName().isEmpty() && Boolean.TRUE.equals(!filterApplied)) {
            SceneLoader.loadProductSearchScene("product_search", "Search", Optional.empty());
            return;
        }

        SceneLoader.loadProductSearchScene("product_search", "Search", filterSearch);
    }
}