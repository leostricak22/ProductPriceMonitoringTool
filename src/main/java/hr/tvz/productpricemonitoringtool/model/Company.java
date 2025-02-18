package hr.tvz.productpricemonitoringtool.model;

import java.util.Objects;

public class Company extends Entity {

    private Address address;
    private String joinCode;

    public Company(Builder builder) {
        super(builder.id, builder.name);
        this.address = builder.address;
        this.joinCode = builder.joinCode;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public static class Builder {
        private final Long id;
        private final String name;
        private Address address;
        private String joinCode;

        public Builder(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Builder joinCode(String joinCode) {
            this.joinCode = joinCode;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(address, company.address);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address);
    }
}