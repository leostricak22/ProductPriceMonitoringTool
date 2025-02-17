package hr.tvz.productpricemonitoringtool.util;

import com.google.gson.Gson;
import hr.tvz.productpricemonitoringtool.exception.UnsuccessfulHTTPResponseCode;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.json.Geolocation;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

public class GeocodeAPI {

    private GeocodeAPI() {}

    public static Address fetchAddressFromLonAndLat(BigDecimal latitude, BigDecimal longitude) throws IOException, UnsuccessfulHTTPResponseCode {
        String jsonString = FetchFromAPI.getJSON(new URL(String.format(
                "%s/reverse?lon=%s&lat=%s&format=json&addressdetails=1",
                Constants.GEOLOCATION_API_URL, longitude, latitude)));

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
