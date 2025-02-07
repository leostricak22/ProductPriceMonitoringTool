package hr.tvz.productpricemonitoringtool.model.dbo;

import hr.tvz.productpricemonitoringtool.model.Entity;

public class CompanyDBO extends Entity {

    private final Long addressId;

    public CompanyDBO(Builder builder) {
        super(builder.id, builder.name);
        this.addressId = builder.addressId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public static class Builder {
        private final Long id;
        private final String name;
        private Long addressId;

        public Builder(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder addressId(Long addressId) {
            this.addressId = addressId;
            return this;
        }

        public CompanyDBO build() {
            return new CompanyDBO(this);
        }
    }
}
