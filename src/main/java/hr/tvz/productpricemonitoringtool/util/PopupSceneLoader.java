package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.controller.*;
import hr.tvz.productpricemonitoringtool.controller.admin.*;
import hr.tvz.productpricemonitoringtool.main.ProductPriceMonitoringToolApplication;
import hr.tvz.productpricemonitoringtool.model.*;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.model.dbo.UserCompanyDBO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class PopupSceneLoader {

    private static final Logger logger = LoggerFactory.getLogger(PopupSceneLoader.class);

    private PopupSceneLoader() {}

    public static Optional<FXMLLoader> loadPopupScene(String fxmlFileName, String title) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = createPopupStage(title, scene);

            popupStage.showAndWait();

            return Optional.of(fxmlLoader);
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
        return Optional.empty();
    }

    public static Optional<FXMLLoader> loadMapPickerPopupScene(String fxmlFileName, String title, Optional<Address> previousAddress) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = createPopupStage(title, scene);

            MapPickerController mapPickerController = fxmlLoader.getController();
            mapPickerController.initialize(previousAddress);

            popupStage.showAndWait();

            return Optional.of(fxmlLoader);
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }

        return Optional.empty();
    }

    public static Optional<FXMLLoader> loadProductFormPopupScene(String fxmlFileName, String title, Optional<Product> product) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = createPopupStage(title, scene);

            AdminProductFormController productFormController = fxmlLoader.getController();
            productFormController.initialize(product);

            popupStage.showAndWait();

            return Optional.of(fxmlLoader);
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }

        return Optional.empty();
    }

    public static Optional<FXMLLoader> loadCompanyFormPopupScene(String fxmlFileName, String title, Optional<Company> company) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = createPopupStage(title, scene);

            AdminCompanyFormController companyFormController = fxmlLoader.getController();
            companyFormController.initialize(company);

            popupStage.showAndWait();

            return Optional.of(fxmlLoader);
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }

        return Optional.empty();
    }

    public static void loadUsersPopupScene(String fxmlFileName, String title, Optional<User> user) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);
            Stage popupStage = createPopupStage(title, scene);

            AdminUserFormController adminUserFormController = fxmlLoader.getController();
            adminUserFormController.initialize(user);

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static void loadCategoryPopupScene(String fxmlFileName, String title, Optional<Category> category) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load());
            Stage popupStage = createPopupStage(title, scene);

            AdminCategoryFormController adminCategoryFormController = fxmlLoader.getController();
            adminCategoryFormController.initialize(category);

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static void loadAddressPopupScene(String fxmlFileName, String title, Optional<Address> address) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);
            Stage popupStage = createPopupStage(title, scene);

            AdminAddressFormController adminAddressFormController = fxmlLoader.getController();
            adminAddressFormController.initialize(address);

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static void loadCompanyProductPopupScene(String fxmlFileName, String title, Optional<CompanyProductDBO> companyProduct) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = createPopupStage(title, scene);

            AdminCompanyProductFormController adminCompanyProductFormController = fxmlLoader.getController();
            adminCompanyProductFormController.initialize(companyProduct);

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static void loadCompanyUsersFormPopupScene(String fxmlFileName, String title, Optional<UserCompanyDBO> userCompany) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);
            Stage popupStage = createPopupStage(title, scene);

            AdminCompanyUsersFormController adminCompanyUsersFormController = fxmlLoader.getController();
            adminCompanyUsersFormController.initialize(userCompany);

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    private static Stage createPopupStage(String title, Scene scene) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(ProductPriceMonitoringToolApplication.getMainStage());
        return popupStage;
    }

    public static void loadProductCompanyGraphPopupScene(String fxmlFileName, String title, Company company) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = createPopupStage(title, scene);

            CompanyProductChartController companyProductChartController = fxmlLoader.getController();
            companyProductChartController.initialize(company);

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }
}