package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.FXMLLoaderException;
import javafx.fxml.FXMLLoader;

import java.net.URL;

public class FXMLLoaderHelper {

    public static FXMLLoader fxmlFilePath(String fileName) {
        try {
            String filePath = String.format(Constants.RELATIVE_FXML_PATH+"%s", fileName);
            return new FXMLLoader(new URL(filePath));
        } catch (Exception e) {
            throw new FXMLLoaderException("Error loading FXML file: " + fileName, e);
        }
    }
}