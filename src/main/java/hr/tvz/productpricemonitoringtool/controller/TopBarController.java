package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.FilterSearch;
import hr.tvz.productpricemonitoringtool.model.PriceNotification;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;

import java.util.HashSet;
import java.util.Optional;

public class TopBarController {

    @FXML public Circle userProfilePictureCircle;
    @FXML public Rectangle notificationIconRectangle;
    @FXML public TextField searchProductsTextField;
    @FXML public Button filterButton;

    private Boolean filterApplied = false;
    private FilterSearch filterSearch = new FilterSearch();

    private final CompanyRepository companyRepository = new CompanyRepository();
    private final ProductRepository productRepository = new ProductRepository();

    private static TopBarController instance;

    public TopBarController() {
        instance = this;
    }

    public static TopBarController getInstance() {
        return instance;
    }

    public void initialize() {
        Image image = Session.getUserProfilePicture();
        userProfilePictureCircle.setFill(new ImagePattern(image));

        notificationIconRectangle.setFill(new ImagePattern(
                new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/notifications.png")));

        PriceNotification priceNotification = new PriceNotification(
                new HashSet<>(Session.getLoggedInUser().get().getCompanies()));

        try {
            priceNotification.checkPriceChange();
            if (!PriceNotification.newCompanyProductRecords.isEmpty()) {
                changeNotificationBellIcon();
            }
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while checking price change.");
        }
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

    public void handleNotifications() {
        Popup notificationsPopup;
        notificationsPopup = new Popup();
        notificationsPopup.setAutoHide(true);

        VBox notificationBox = new VBox();
        notificationBox.getStyleClass().add("notification-box");

        PriceNotification.newCompanyProductRecords
                .forEach(companyProductRecord -> {
                    try {
                        createNewNotification(notificationBox,
                                "Company " + companyRepository.findById(companyProductRecord.getCompanyId()).get().getName() +
                                        " changed or added a price for the product " + productRepository.findById(companyProductRecord.getProductId()).get().getName()
                        );
                    } catch (DatabaseConnectionActiveException e) {
                        AlertDialog.showErrorDialog("Error while fetching company or product name.");
                    }
                });

        PriceNotification priceNotification = new PriceNotification(Session.getLoggedInUser().get().getCompanies());
        priceNotification.save();

        if (PriceNotification.newCompanyProductRecords.isEmpty()) {
            createNoNewNotifications(notificationBox);
        }

        notificationIconRectangle.setFill(new ImagePattern(
                new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/notifications.png")));

        ScrollPane scrollPane = new ScrollPane(notificationBox);
        scrollPane.getStyleClass().add("notification-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(1000);
        scrollPane.setPrefWidth(700);

        notificationBox.setFocusTraversable(false);
        scrollPane.setFocusTraversable(false);

        scrollPane.getStylesheets().add("file:src/main/resources/hr/tvz/productpricemonitoringtool/styles/notification.css");

        notificationsPopup.getContent().add(scrollPane);

        if (!notificationsPopup.isShowing()) {
            Point2D pos = notificationIconRectangle.localToScreen(
                    0, notificationIconRectangle.getBoundsInLocal().getHeight());
            notificationsPopup.show(notificationIconRectangle, pos.getX(), pos.getY());
        } else {
            notificationsPopup.hide();
        }
    }

    private void createNewNotification(VBox notificationBox, String notificationText) {
        HBox notificationItem = new HBox();
        notificationItem.getStyleClass().add("notification-item");

        ImageView notificationImageView = new ImageView(
                new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/price.png"));

        notificationImageView.setFitHeight(30);
        notificationImageView.setFitWidth(30);

        Label notificationLabel = new Label(notificationText);
        notificationLabel.getStyleClass().add("notification-label");

        notificationItem.getChildren().addAll(notificationImageView, notificationLabel);
        notificationBox.getChildren().add(notificationItem);
    }

    private void createNoNewNotifications(VBox notificationBox) {
        HBox notificationItem = new HBox();
        notificationItem.getStyleClass().add("notification-item");

        Label notificationLabel = new Label("No new notifications.");
        notificationLabel.getStyleClass().add("notification-label");

        notificationItem.getChildren().add(notificationLabel);
        notificationBox.getChildren().add(notificationItem);
    }

    public void changeNotificationBellIcon() {
        notificationIconRectangle.setFill(new ImagePattern(
                new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/notifications_new.png")));
    }
}