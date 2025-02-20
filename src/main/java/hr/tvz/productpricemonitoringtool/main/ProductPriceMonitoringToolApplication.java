package hr.tvz.productpricemonitoringtool.main;

import hr.tvz.productpricemonitoringtool.model.AuditLogManager;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class of the application.
 * Starts the application and loads the login scene.
 */
public class ProductPriceMonitoringToolApplication extends Application {

    private static Stage mainStage;

    /**
     * Starts the application and loads the login scene.
     * @param stage Stage of the application.
     */
    @Override
    public void start(Stage stage) {
        AuditLogManager.load();

        setMainStage(stage);
        setStageConfiguration(stage);
        SceneLoader.loadScene("login", "Login");

        stage.show();
    }

    /**
     * Main method of the application.
     * @param args Arguments of the application.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Gets the main stage of the application.
     */
    public static Stage getMainStage() {
        return mainStage;
    }

    /**
     * Sets the main stage of the application.
     */
    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Sets the configuration of the stage.
     */
    public static void setStageConfiguration(Stage stage) {
        stage.setTitle("Product Price Monitoring Tool");
        stage.setWidth(Constants.SCENE_WIDTH);
        stage.setHeight(Constants.SCENE_HEIGHT);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(Constants.APPLICATION_ICON));

        stage.setMinWidth(Constants.SCENE_MIN_WIDTH);
        stage.setMinHeight(Constants.SCENE_MIN_HEIGHT);
    }
}
