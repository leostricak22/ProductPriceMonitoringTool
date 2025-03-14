package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.StaffNotification;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.thread.CheckNotificationsThread;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;

/**
 * Session class.
 * Contains methods for managing user session.
 */
public class Session {

    private static final Logger log = LoggerFactory.getLogger(Session.class);
    private static Optional<User> loggedInUser = Optional.empty();
    private static Optional<Company> selectedCompany = Optional.empty();
    private static Optional<Product> selectedProduct = Optional.empty();

    private Session() {}

    /**
     * Method for setting logged in user.
     * @param user User.
     */
    public static void setLoggedInUser(User user) {
        loggedInUser = Optional.of(user);
        CheckNotificationsThread.start();

        try {
            StaffNotification staffNotification = new StaffNotification();
            staffNotification.checkStaffChange();
        } catch (DatabaseConnectionActiveException e) {
            log.error("Failed to check staff changes.");
        }
    }

    public static Optional<User> getLoggedInUser() {
        return loggedInUser;
    }

    public static void setSelectedCompany(Company company) {
        selectedCompany = Optional.of(company);
    }

    public static void setSelectedCompany(Optional<Company> company) {
        selectedCompany = company;
    }

    public static Optional<Company> getSelectedCompany() {
        return selectedCompany;
    }

    public static void setSelectedProduct(Product product) {
        selectedProduct = Optional.of(product);
    }

    public static Optional<Product> getSelectedProduct() {
        return selectedProduct;
    }

    /**
     * Method for getting user profile picture.
     * @return Image.
     */
    public static Image getUserProfilePicture() {
        if (loggedInUser.isEmpty()) {
            throw new AuthenticationException("No user is logged in.");
        }

        for (String extension : Constants.IMAGE_EXTENSIONS) {
            String filePath = "files/user/" + loggedInUser.get().getId() + extension.substring(1);
            File userProfilePictureFile = new File(filePath);

            if (!userProfilePictureFile.exists())
                continue;

            return FileUtil.cropImageToSquare(new Image(userProfilePictureFile.toURI().toString()));
        }

        return FileUtil.cropImageToSquare(
                new Image("file:files/user/default_profile_picture.png"));
    }

    /**
     * Method for getting guest user.
     * @return User.
     *         Guest user.
     */
    public static User getGuestUser() {
        return new User.Builder(0L, "Customer")
                .role(Role.CUSTOMER)
                .companies(new HashSet<>())
                .build();
    }

    /**
     * Method for logging out.
     */
    public static void logout() {
        loggedInUser = Optional.empty();
        selectedCompany = Optional.empty();
        selectedProduct = Optional.empty();

        CheckNotificationsThread.stop();
    }
}
