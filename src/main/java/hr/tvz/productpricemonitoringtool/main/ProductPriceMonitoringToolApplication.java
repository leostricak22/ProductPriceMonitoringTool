package hr.tvz.productpricemonitoringtool.main;

import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class ProductPriceMonitoringToolApplication extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) {
        setMainStage(stage);
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