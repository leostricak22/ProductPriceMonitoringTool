package hr.tvz.productpricemonitoringtool.model;

public class Company extends Entity {

    Address address;

    public Company(Builder builder) {
        super(builder.id, builder.name);
        this.address = builder.address;
    }

    public static class Builder {
        private final Long id;
        private final String name;
        private Address address;

        public Builder(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}