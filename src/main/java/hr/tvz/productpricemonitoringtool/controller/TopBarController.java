package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.FilterSearch;
import hr.tvz.productpricemonitoringtool.model.PriceNotification;
import hr.tvz.productpricemonitoringtool.model.StaffNotification;
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

import java.util.Optional;

/**
 * Controller for the top bar view.
 * Handles the top bar actions.
 * Redirects to the user edit view.
 * Handles the search and filter actions.
 * Handles the notifications popup.
 * @see FilterSearch
 * @see PriceNotification
 * @see StaffNotification
 * @see CompanyRepository
 * @see ProductRepository
 * @see AlertDialog
 * @see SceneLoader
 * @see Session
 */
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

    /**
     * Initializes the view.
     */
    public TopBarController() {
        setInstance(this);
    }

    /**
     * Gets the instance of the top bar controller.
     */
    public static TopBarController getInstance() {
        return instance;
    }

    /**
     * Sets the instance of the top bar controller.
     */
    public static void setInstance(TopBarController instance) {
        TopBarController.instance = instance;
    }

    /**
     * Initializes the view.
     * Sets the user profile picture.
     * Checks for price and staff changes.
     */
    public void initialize() {
        Image image = Session.getUserProfilePicture();
        userProfilePictureCircle.setFill(new ImagePattern(image));

        notificationIconRectangle.setFill(new ImagePattern(
                new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/notifications.png")));

        PriceNotification priceNotification = new PriceNotification();
        StaffNotification staffNotification = new StaffNotification();

        try {
            priceNotification.checkPriceChange();
            if (!PriceNotification.getNewCompanyProductRecords().isEmpty()) {
                changeNotificationBellIcon();
            }

            staffNotification.checkStaffChange();
            if (!StaffNotification.getNewUserCompanyDBORecords().isEmpty()) {
                changeNotificationBellIcon();
            }
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Error while checking price change.");
        }
    }

    /**
     * Handles the user edit redirect.
     * Redirects to the user edit view.
     */
    public void handleUserEditRedirect() {
        SceneLoader.loadScene("user_edit", "Edit User");
    }

    /**
     * Handles the filter action.
     * Opens the filter popup.
     * Sets the filter search.
     */
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

    /**
     * Handles the search action.
     * Sets the product name for the search.
     * Redirects to the product search view.
     */
    public void handleSearch() {
        filterSearch.setProductName(searchProductsTextField.getText().isEmpty() ?
                Optional.empty() : Optional.of(searchProductsTextField.getText()));
        if (filterSearch.getProductName().isEmpty() && Boolean.TRUE.equals(!filterApplied)) {
            SceneLoader.loadProductSearchScene("product_search", "Search", Optional.empty());
            return;
        }

        SceneLoader.loadProductSearchScene("product_search", "Search", filterSearch);
    }

    /**
     * Handles the notifications popup.
     * Creates the notifications popup.
     * Sets the notifications.
     */
    public void handleNotifications() {
        Popup notificationsPopup;
        notificationsPopup = new Popup();
        notificationsPopup.setAutoHide(true);

        VBox notificationBox = new VBox();
        notificationBox.getStyleClass().add("notification-box");

        PriceNotification.getNewCompanyProductRecords()
                .forEach(companyProductRecord -> {
                    try {
                        createNewNotification(notificationBox,
                                "Company " + companyRepository.findById(companyProductRecord.getCompanyId()).get().getName() +
                                        " changed or added a price for the product " + productRepository.findById(companyProductRecord.getProductId()).get().getName(),
                                "file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/price.png"
                        );
                    } catch (DatabaseConnectionActiveException e) {
                        AlertDialog.showErrorDialog("Error while fetching company or product name.");
                    }
                });

        StaffNotification.getNewUserCompanyDBORecords()
                .forEach(userCompanyDBO -> {
                    try {
                        createNewNotification(notificationBox,
                                "Company " + companyRepository.findById(userCompanyDBO.getCompanyId()).get().getName() +
                                        " added a new staff member " + Session.getLoggedInUser().get().getName() + " " + Session.getLoggedInUser().get().getSurname(),
                                "file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/staff.png"
                        );
                    } catch (DatabaseConnectionActiveException e) {
                        AlertDialog.showErrorDialog("Error while fetching company name.");
                    }
                });

        PriceNotification priceNotification = new PriceNotification();
        priceNotification.save();

        StaffNotification staffNotification = new StaffNotification();
        staffNotification.save();

        if (PriceNotification.getNewCompanyProductRecords().isEmpty() &&
                StaffNotification.getNewUserCompanyDBORecords().isEmpty()) {
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

    /**
     * Creates a new notification item.
     * @param notificationBox The notification box.
     * @param notificationText The notification text.
     * @param imagePath The image path.
     */
    private void createNewNotification(VBox notificationBox, String notificationText, String imagePath) {
        HBox notificationItem = new HBox();
        notificationItem.getStyleClass().add("notification-item");

        ImageView notificationImageView = new ImageView(
                new Image(imagePath));

        notificationImageView.setFitHeight(30);
        notificationImageView.setFitWidth(30);

        Label notificationLabel = new Label(notificationText);
        notificationLabel.getStyleClass().add("notification-label");

        notificationItem.getChildren().addAll(notificationImageView, notificationLabel);
        notificationBox.getChildren().add(notificationItem);
    }


    /**
     * Creates a no new notifications item.
     * @param notificationBox The notification box.
     */
    private void createNoNewNotifications(VBox notificationBox) {
        HBox notificationItem = new HBox();
        notificationItem.getStyleClass().add("notification-item");

        Label notificationLabel = new Label("No new notifications.");
        notificationLabel.getStyleClass().add("notification-label");

        notificationItem.getChildren().add(notificationLabel);
        notificationBox.getChildren().add(notificationItem);
    }

    /**
     * Changes the notification bell icon.
     */
    public void changeNotificationBellIcon() {
        notificationIconRectangle.setFill(new ImagePattern(
                new Image("file:src/main/resources/hr/tvz/productpricemonitoringtool/images/icons/notifications_new.png")));
    }
}