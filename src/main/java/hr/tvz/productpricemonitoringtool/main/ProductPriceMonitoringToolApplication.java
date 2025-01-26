package hr.tvz.productpricemonitoringtool.main;

import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;

public class ProductPriceMonitoringToolApplication extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) {
        setMainStage(stage);

        stage.setTitle("Product Price Monitoring Tool");
        stage.setWidth(Constants.SCENE_WIDTH);
        stage.setHeight(Constants.SCENE_HEIGHT);
        stage.setMaximized(true);
        //stage.getIcons().add(new Image("file:logo.png"));
        stage.getIcons().add(new Image(Constants.APPLICATION_ICON));

        stage.setMinWidth(Constants.SCENE_MIN_WIDTH);
        stage.setMinHeight(Constants.SCENE_MIN_HEIGHT);

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
}
