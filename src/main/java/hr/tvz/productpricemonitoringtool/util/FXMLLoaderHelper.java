package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.FXMLLoaderException;
import javafx.fxml.FXMLLoader;

import java.net.URI;

public class FXMLLoaderHelper {

    private FXMLLoaderHelper() {}

    public static FXMLLoader fxmlFilePath(String fileName) {
        try {
            String filePath = String.format(Constants.RELATIVE_FXML_PATH+"%s", fileName);
            return new FXMLLoader((new URI(filePath)).toURL());
        } catch (Exception e) {
            throw new FXMLLoaderException("Error loading FXML file: " + fileName, e);
        }
    }
}