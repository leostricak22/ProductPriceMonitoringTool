package hr.tvz.productpricemonitoringtool.main;

import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ProductPriceMonitoringToolApplication extends Application {

    private static Stage mainStage;

    CategoryRepository categoryRepository = new CategoryRepository();

    @Override
    public void start(Stage stage) {
        setMainStage(stage);
        setStageConfiguration(stage);
        SceneLoader.loadScene("login", "Login");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

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
