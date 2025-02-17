package hr.tvz.productpricemonitoringtool.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropsUtil {

    private PropsUtil() {}

    public static Properties getProperties(String propertiesFilePath) throws IOException {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(propertiesFilePath)) {
            props.load(reader);
        }

        return props;
    }
}
