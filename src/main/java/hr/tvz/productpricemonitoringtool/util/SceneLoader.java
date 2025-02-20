package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.controller.ProductSearchController;
import hr.tvz.productpricemonitoringtool.main.ProductPriceMonitoringToolApplication;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.FilterSearch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * SceneLoader class.
 * Contains methods for loading scenes.
 */
public class SceneLoader {

    private static final Logger logger = LoggerFactory.getLogger(SceneLoader.class);

    private SceneLoader() {}

    /**
     * Method for loading scene.
     * @param fxmlFileName FXML file name.
     */
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

    /**
     * Method for loading popup scene.
     * @param fxmlFileName FXML file name.
     * @param title Title.
     * @return Optional FXMLLoader.
     *         Optional FXMLLoader object.
     */
    public static Optional<FXMLLoader> loadPopupScene(String fxmlFileName, String title) {
        return PopupSceneLoader.loadPopupScene(fxmlFileName, title);
    }

    /**
     * Method for loading product search scene.
     * @param fxmlFileName FXML file name.
     * @param title Title.
     * @param category Category.
     */
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
}