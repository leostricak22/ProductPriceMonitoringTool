package hr.tvz.productpricemonitoringtool.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.isNull;

public class Address extends Entity implements Serializable {

    private BigDecimal latitude;
    private BigDecimal longitude;
    private String road;
    private String houseNumber;
    private String city;
    private String town;
    private String village;
    private String country;

    public Address(Builder builder) {
        super(builder.id, builder.road);
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.road = builder.road;
        this.houseNumber = builder.houseNumber;
        this.city = builder.city;
        this.town = builder.town;
        this.village = builder.village;
        this.country = builder.country;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

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

    public static class Builder {
        private final Long id;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String road;
        private String houseNumber;
        private String city;
        private String town;
        private String village;
        private String country;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder road(String road) {
            this.road = road;
            return this;
        }

        public Builder houseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder town(String town) {
            this.town = town;
            return this;
        }

        public Builder village(String village) {
            this.village = village;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }

    public String getAddress() {
        StringBuilder address = new StringBuilder();
        if (isNull(road) || road.equals("?")) {
            address.append("?");
        } else {
            address.append(road);
        }

        if (!isNull(houseNumber) && !houseNumber.equals("?")) {
            address.append(" ").append(houseNumber);
        }

        if (!isNull(city) && !city.equals("?")) {
            address.append(", ").append(city);
        }

        if (!isNull(town) && !town.equals("?")) {
            address.append(", ").append(town);
        }

        if (!isNull(village) && !village.equals("?")) {
            address.append(", ").append(village);
        }

        if (!isNull(country) && !country.equals("?")) {
            address.append(", ").append(country);
        }

        return address.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(latitude, address.latitude) && Objects.equals(longitude, address.longitude) && Objects.equals(road, address.road) && Objects.equals(houseNumber, address.houseNumber) && Objects.equals(city, address.city) && Objects.equals(town, address.town) && Objects.equals(village, address.village) && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, road, houseNumber, city, town, village, country);
    }
}
