package hr.tvz.productpricemonitoringtool.model;

import java.math.BigDecimal;

public class Coordinates {
    private BigDecimal latitude;
    private BigDecimal longitude;

    private Coordinates(Builder builder) {
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
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

    public static class Builder {
        private BigDecimal latitude;
        private BigDecimal longitude;

        public Builder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public Coordinates build() {
            return new Coordinates(this);
        }
    }
}
