package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.UnsuccessfulHTTPResponseCode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

public class FetchFromAPI {

    private FetchFromAPI() {}

    public static String getJSON(URL url) throws IOException, UnsuccessfulHTTPResponseCode {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Properties props = PropsUtil.getProperties(Constants.SECRET_PROPERTIES_FILE);
        conn.setRequestProperty("User-Agent", props.getProperty("NOMINATIM_AUTH_TOKEN"));
        conn.connect();

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new UnsuccessfulHTTPResponseCode("HttpResponseCode: " + responseCode);
        }

        StringBuilder jsonString = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());

        while (scanner.hasNext()) {
            jsonString.append(scanner.nextLine());
        }

        scanner.close();

        return jsonString.toString();
    }
}
