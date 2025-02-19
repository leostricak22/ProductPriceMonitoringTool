package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.controller.*;
import hr.tvz.productpricemonitoringtool.main.ProductPriceMonitoringToolApplication;
import hr.tvz.productpricemonitoringtool.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class SceneLoader {

    private static final Logger logger = LoggerFactory.getLogger(SceneLoader.class);

    private SceneLoader() {}

    public static void loadScene(String fxmlFileName, String title) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);

            Scene scene = new Scene(fxmlLoader.load());

            ProductPriceMonitoringToolApplication.getMainStage().setTitle(title);
            ProductPriceMonitoringToolApplication.getMainStage().setScene(scene);
            ProductPriceMonitoringToolApplication.getMainStage().show();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static Optional<FXMLLoader> loadPopupScene(String fxmlFileName, String title) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = new Stage();

            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(ProductPriceMonitoringToolApplication.getMainStage());

            popupStage.showAndWait();

            return Optional.of(fxmlLoader);
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
        return Optional.empty();
    }

    public static void loadProductSearchScene(String fxmlFileName, String title, Optional<Category> category) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);

            Scene scene = new Scene(fxmlLoader.load());

            ProductPriceMonitoringToolApplication.getMainStage().setTitle(title);
            ProductPriceMonitoringToolApplication.getMainStage().setScene(scene);

            ProductSearchController productSearchController = fxmlLoader.getController();
            productSearchController.initialize(category);

            ProductPriceMonitoringToolApplication.getMainStage().show();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static void loadProductSearchScene(String fxmlFileName, String title, FilterSearch filterSearch) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);

            Scene scene = new Scene(fxmlLoader.load());

            ProductPriceMonitoringToolApplication.getMainStage().setTitle(title);
            ProductPriceMonitoringToolApplication.getMainStage().setScene(scene);

            ProductSearchController productSearchController = fxmlLoader.getController();
            productSearchController.initialize(filterSearch);

            ProductPriceMonitoringToolApplication.getMainStage().show();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static void loadProductCompanyGraphPopupScene(String fxmlFileName, String title, Company company) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = new Stage();

            CompanyProductChartController companyProductChartController = fxmlLoader.getController();
            companyProductChartController.initialize(company);

            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(ProductPriceMonitoringToolApplication.getMainStage());

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }

    public static Optional<FXMLLoader> loadMapPickerPopupScene(String fxmlFileName, String title, Optional<Address> previousAddress) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            Stage popupStage = new Stage();

            MapPickerController mapPickerController = fxmlLoader.getController();
            mapPickerController.initialize(previousAddress);

            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(ProductPriceMonitoringToolApplication.getMainStage());

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
            Stage popupStage = new Stage();

            AdminProductFormController productFormController = fxmlLoader.getController();
            productFormController.initialize(product);

            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(ProductPriceMonitoringToolApplication.getMainStage());

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
            Stage popupStage = new Stage();

            AdminCompanyFormController companyFormController = fxmlLoader.getController();
            companyFormController.initialize(company);

            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(ProductPriceMonitoringToolApplication.getMainStage());

            popupStage.showAndWait();

            return Optional.of(fxmlLoader);
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }

        return Optional.empty();
    }

    public static void loadUsersScene(String fxmlFileName, String title, Optional<User> user) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + Constants.SCENE_EXTENSION);
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);
            Stage popupStage = new Stage();

            AdminUserFormController adminUserFormController = fxmlLoader.getController();
            adminUserFormController.initialize(user);

            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(ProductPriceMonitoringToolApplication.getMainStage());

            popupStage.showAndWait();
        } catch (IOException e) {
            AlertDialog.showErrorDialog(Constants.ERROR_LOADING_SCENE_MESSAGE + fxmlFileName);
            logger.error(Constants.ERROR_LOADING_SCENE_MESSAGE + "{}", fxmlFileName, e);
        }
    }
}