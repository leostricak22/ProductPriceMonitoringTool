package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.FXMLLoaderException;
import javafx.fxml.FXMLLoader;

import java.net.URI;

/**
 * FXMLLoaderHelper class.
 * Contains method for loading FXML file.
 */
public class FXMLLoaderHelper {

    private FXMLLoaderHelper() {}

    /**
     * Method for loading FXML file.
     * @param fileName File name.
     *                 Name of the FXML file.
     * @return FXMLLoader.
     *         FXMLLoader object.
     */
    public static FXMLLoader fxmlFilePath(String fileName) {
        try {
            String filePath = String.format(Constants.RELATIVE_FXML_PATH+"%s", fileName);
            return new FXMLLoader((new URI(filePath)).toURL());
        } catch (Exception e) {
            throw new FXMLLoaderException("Error loading FXML file: " + fileName, e);
        }
    }
}