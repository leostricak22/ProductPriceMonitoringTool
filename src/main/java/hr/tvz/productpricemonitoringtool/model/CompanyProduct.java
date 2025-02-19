package hr.tvz.productpricemonitoringtool.model;

import java.io.*;
import java.time.LocalDateTime;

public class CompanyProduct extends Entity implements Serializable {

    private Product product;
    private Company company;
    private Price price;
    private LocalDateTime createdAt;

    public CompanyProduct(Builder builder) {
        super(builder.id, builder.name);
        this.product = builder.product;
        this.company = builder.company;
        this.price = builder.price;
        this.createdAt = builder.createdAt;
    }

    public Company getCompany() {
        return company;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Company company;
        private Price price;
        private Product product;
        private LocalDateTime createdAt;

        public Builder(Long id) {
            this.id = id;
            this.name = "";
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder company(Company company) {
            this.company = company;
            return this;
        }

        public Builder price(Price price) {
            this.price = price;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CompanyProduct build() {
            return new CompanyProduct(this);
        }
    }
}
