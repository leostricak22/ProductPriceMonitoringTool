package hr.tvz.productpricemonitoringtool.util;

import java.util.List;

public class Constants {

    private Constants() {}

    public static final String RELATIVE_FXML_PATH =
            "file:./src/main/resources/hr/tvz/productpricemonitoringtool/";
    public static final String DATABASE_PROPERTIES_FILE =
            "./src/main/resources/database.properties";
    public static final String APPLICATION_ICON =
            "file:./src/main/resources/hr/tvz/productpricemonitoringtool/images/logo.png";
    public static final Integer SCENE_WIDTH = 1065;
    public static final Integer SCENE_HEIGHT = 700;
    public static final Integer SCENE_MIN_WIDTH = 480;
    public static final Integer SCENE_MIN_HEIGHT = 600;
    public static final List<String> IMAGE_EXTENSIONS = List.of("*.jpg", "*.jpeg", "*.png");
    public static final String USER_PROFILE_PICTURES_PATH = "files/user/";
}