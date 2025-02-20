package hr.tvz.productpricemonitoringtool.model.json;

import java.math.BigDecimal;

/**
 * Geolocation JSON model.
 * Represents the geolocation in the JSON model.
 */
public class Geolocation {
    private AddressGeolocation address;
    private BigDecimal lat;
    private BigDecimal lon;

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public AddressGeolocation getAddress() {
        return address;
    }

    public void setAddress(AddressGeolocation address) {
        this.address = address;
    }
}