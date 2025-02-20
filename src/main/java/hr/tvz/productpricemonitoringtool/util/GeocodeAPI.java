package hr.tvz.productpricemonitoringtool.util;

import com.google.gson.Gson;
import hr.tvz.productpricemonitoringtool.exception.UnsuccessfulHTTPResponseCode;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.json.Geolocation;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * GeocodeAPI class.
 * Contains method for fetching address from longitude and latitude.
 */
public class GeocodeAPI {

    private GeocodeAPI() {}

    /**
     * Fetch address from longitude and latitude.
     * @param latitude Latitude.
     *                 Latitude of the location.
     * @param longitude Longitude.
     *                  Longitude of the location.
     * @return Address.
     *         Address object.
     * @throws IOException If an I/O error occurs.
     * @throws UnsuccessfulHTTPResponseCode If HTTP response code is not 200.
     * @throws URISyntaxException If URI syntax error occurs.
     */
    public static Address fetchAddressFromLonAndLat(BigDecimal latitude, BigDecimal longitude) throws IOException, UnsuccessfulHTTPResponseCode, URISyntaxException {
        String jsonString = FetchFromAPI.getJSON(new URI(String.format(
                "%s/reverse?lon=%s&lat=%s&format=json&addressdetails=1",
                Constants.GEOLOCATION_API_URL, longitude, latitude)).toURL());

        Gson gson = new Gson();
        Geolocation geolocation = gson.fromJson(jsonString, Geolocation.class);

        return new Address.Builder(0L)
                .latitude(latitude)
                .longitude(longitude)
                .road(geolocation.getAddress().getRoad())
                .houseNumber(geolocation.getAddress().getHouseNumber())
                .city(geolocation.getAddress().getCity())
                .town(geolocation.getAddress().getTown())
                .village(geolocation.getAddress().getVillage())
                .country(geolocation.getAddress().getCountry())
                .build();
    }
}
