package hr.tvz.productpricemonitoringtool.model.dbo;

import hr.tvz.productpricemonitoringtool.model.Entity;

public class ProductDBO extends Entity {

    private Long categoryId;

    public ProductDBO(Builder builder) {
        super(builder.id, builder.name);
        this.categoryId = builder.categoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public static class Builder {
        private final Long id;
        private String name;
        private Long categoryId;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public ProductDBO build() {
            return new ProductDBO(this);
        }
    }
}
