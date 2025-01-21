package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.main.ProductPriceMonitoringToolApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SceneLoader {

    private static final Logger logger = LoggerFactory.getLogger(SceneLoader.class);

    private SceneLoader() {}

    public static void loadScene(String fxmlFileName, String title) {
        try {
            FXMLLoader fxmlLoader = FXMLLoaderHelper.fxmlFilePath(fxmlFileName + ".fxml");

            Scene scene = new Scene(fxmlLoader.load(), Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

            ProductPriceMonitoringToolApplication.getMainStage().setTitle(title);
            ProductPriceMonitoringToolApplication.getMainStage().setScene(scene);
            ProductPriceMonitoringToolApplication.getMainStage().show();
        } catch (IOException e) {
            AlertDialog.showErrorDialog("Error", "Error loading scene: " + fxmlFileName);
            logger.error("Error loading scene: {}", fxmlFileName, e);
        }
    }
}