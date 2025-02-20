package hr.tvz.productpricemonitoringtool.model.dbo;

import hr.tvz.productpricemonitoringtool.model.Entity;

/**
 * Company database object.
 * Represents the company in the database.
 */
public class CompanyDBO extends Entity {

    private final Long addressId;
    private final String joinCode;

    public CompanyDBO(Builder builder) {
        super(builder.id, builder.name);
        this.addressId = builder.addressId;
        this.joinCode = builder.joinCode;
    }

    public Long getAddressId() {
        return addressId;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public static class Builder {
        private final Long id;
        private final String name;
        private Long addressId;
        private String joinCode;

        public Builder(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder addressId(Long addressId) {
            this.addressId = addressId;
            return this;
        }

        public Builder joinCode(String joinCode) {
            this.joinCode = joinCode;
            return this;
        }

        public CompanyDBO build() {
            return new CompanyDBO(this);
        }
    }
}
