package hr.tvz.productpricemonitoringtool.model;

public class CompanyProduct {

    private Product product;
    private Company company;
    private Price price;

    public CompanyProduct(Builder builder) {
        this.product = builder.product;
        this.company = builder.company;
        this.price = builder.price;
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

    public static class Builder {
        private Company company;
        private Price price;
        private Product product;

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

        public CompanyProduct build() {
            return new CompanyProduct(this);
        }
    }
}
