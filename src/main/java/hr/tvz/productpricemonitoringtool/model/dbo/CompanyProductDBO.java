package hr.tvz.productpricemonitoringtool.model.dbo;

import hr.tvz.productpricemonitoringtool.model.Entity;
import hr.tvz.productpricemonitoringtool.model.Price;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CompanyProductDBO extends Entity implements Serializable {

    private Long companyId;
    private Long productId;
    private Price price;
    private LocalDateTime createdAt;

    public CompanyProductDBO(Builder builder) {
        super(builder.id, builder.name);
        this.companyId = builder.companyId;
        this.productId = builder.productId;
        this.price = builder.price;
        this.createdAt = builder.createdAt;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
        private Long companyId;
        private Long productId;
        private Price price;
        private LocalDateTime createdAt;

        public Builder(Long id) {
            this.id = id;
            this.name = "";
        }

        public Builder companyId(Long companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder productId(Long productId) {
            this.productId = productId;
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

        public CompanyProductDBO build() {
            return new CompanyProductDBO(this);
        }
    }
}
