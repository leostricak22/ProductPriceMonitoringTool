package hr.tvz.productpricemonitoringtool.model.json;

import com.google.gson.annotations.SerializedName;

/**
 * Address geolocation.
 * Represents the address geolocation.
 */
public class AddressGeolocation {

    private String road;
    @SerializedName("house_number")
    private String houseNumber;
    private String city;
    private String town;
    private String village;
    private String country;

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
