package hr.tvz.productpricemonitoringtool.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * PropsUtil class.
 * Contains method for getting properties.
 */
public class PropsUtil {

    private PropsUtil() {}

    /**
     * Method for getting properties.
     * @param propertiesFilePath Properties file path.
     *                           Path of the properties file.
     * @return Properties.
     *         Properties object.
     * @throws IOException If an I/O error occurs.
     */
    public static Properties getProperties(String propertiesFilePath) throws IOException {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(propertiesFilePath)) {
            props.load(reader);
        }

        return props;
    }
}
