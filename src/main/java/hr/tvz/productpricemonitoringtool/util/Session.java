package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.User;
import javafx.scene.image.Image;

import java.io.File;
import java.util.Optional;

public class Session {

    private static Optional<User> loggedInUser = Optional.empty();
    private static Optional<Company> selectedCompany = Optional.empty();
    private static Optional<Product> selectedProduct = Optional.empty();

    private Session() {}

    public static void setLoggedInUser(User user) {
        loggedInUser = Optional.of(user);
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
}
