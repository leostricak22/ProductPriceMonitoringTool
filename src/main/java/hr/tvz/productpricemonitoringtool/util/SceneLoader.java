package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.controller.ProductSearchController;
import hr.tvz.productpricemonitoringtool.main.ProductPriceMonitoringToolApplication;
import hr.tvz.productpricemonitoringtool.model.Category;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class SceneLoader {

    private static final Logger logger = LoggerFactory.getLogger(SceneLoader.class);

    private SceneLoader() {}

    public static void loadScene(String fxmlFileName, String title) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + ".fxml");

            Scene scene = new Scene(fxmlLoader.load());

            ProductPriceMonitoringToolApplication.getMainStage().setTitle(title);
            ProductPriceMonitoringToolApplication.getMainStage().setScene(scene);
            ProductPriceMonitoringToolApplication.getMainStage().show();
        } catch (IOException e) {
            AlertDialog.showErrorDialog("Error", "Error loading scene: " + fxmlFileName);
            logger.error("Error loading scene: {}", fxmlFileName, e);
        }
    }

    public static void loadProductSearchScene(String fxmlFileName, String title, Optional<Category> category) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + ".fxml");

            Scene scene = new Scene(fxmlLoader.load());

            ProductPriceMonitoringToolApplication.getMainStage().setTitle(title);
            ProductPriceMonitoringToolApplication.getMainStage().setScene(scene);

            ProductSearchController productSearchController = fxmlLoader.getController();
            productSearchController.initialize(category);

            ProductPriceMonitoringToolApplication.getMainStage().show();
        } catch (IOException e) {
            AlertDialog.showErrorDialog("Error", "Error loading scene: " + fxmlFileName);
            logger.error("Error loading scene: {}", fxmlFileName, e);
        }
    }
}