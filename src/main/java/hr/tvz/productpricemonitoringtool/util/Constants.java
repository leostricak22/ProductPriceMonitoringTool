package hr.tvz.productpricemonitoringtool.util;

import java.util.List;

/**
 * Constants class.
 * Contains application constants.
 */
public class Constants {

    private Constants() {}

    public static final String RELATIVE_FXML_PATH =
            "file:./src/main/resources/hr/tvz/productpricemonitoringtool/";
    public static final String RELATIVE_HTML_PATH =
            "./src/main/resources/hr/tvz/productpricemonitoringtool/html/";
    public static final String DATABASE_PROPERTIES_FILE =
            "./src/main/resources/database.properties";
    public static final String SECRET_PROPERTIES_FILE =
            "./src/main/resources/secret.properties";
    public static final String APPLICATION_ICON =
            "file:./src/main/resources/hr/tvz/productpricemonitoringtool/images/logo.png";
    public static final String NO_IMAGE_URL =
            "file:./files/product/no-image.png";
    public static final Integer SCENE_WIDTH = 1065;
    public static final Integer SCENE_HEIGHT = 700;
    public static final Integer SCENE_MIN_WIDTH = 480;
    public static final Integer SCENE_MIN_HEIGHT = 600;
    public static final List<String> IMAGE_EXTENSIONS = List.of("*.jpg", "*.jpeg", "*.png");
    public static final String USER_PROFILE_PICTURES_PATH = "files/user/";
    public static final Long MAX_DB_CONNECTION_WAIT_TIME_IN_SECONDS = 10L;
    public static final String CATEGORY_HIERARCHY_PREFIX = "Category: ";
    public static final String SCENE_EXTENSION = ".fxml";

    public static final String ALERT_ERROR_TITLE = "Error occurred";
    public static final String DATABASE_ACTIVE_CONNECTION_ERROR_MESSAGE =
            "Database connection is active. Please try again later.";
    public static final String ERROR_LOADING_SCENE_MESSAGE = "Error loading scene: ";

    public static final String POSITIVE_BIG_DECIMAL_REGEX = "^[1-9]\\d*(\\.\\d+)?$";
    public static final String BIG_DECIMAL_REGEX = "^\\d*(\\.\\d+)?$";
    public static final String POSITIVE_INTEGER_REGEX = "^[1-9]\\d*$";

    public static final String GEOLOCATION_API_URL = "https://nominatim.openstreetmap.org";

    public static final String SESSION_COMPANY_PRODUCT_SERIALIZATION_FILE =
            "files/session/companyProducts.dat";
    public static final String SESSION_USER_COMPANY_SERIALIZATION_FILE =
            "files/session/userCompany.dat";
    public static final String SESSION_SERIALIZATION_FILE_DIRECTORY =
            "files/session/";

    public static final String TRY_AGAIN_LATER_ERROR_MESSAGE = "Please try again later.";
    public static final String DASHBOARD_TITLE = "Dashboard";
    public static final String DASHBOARD_FILE_NAME = "dashboard";

    public static final String REPOSITORY_FETCH_ERROR_MESSAGE = "Error fetching data from the repository.";
}