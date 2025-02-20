package hr.tvz.productpricemonitoringtool.model.dbo;

import hr.tvz.productpricemonitoringtool.model.Entity;

/**
 * Product database object.
 * Represents the product in the database.
 */
public class ProductDBO extends Entity {

    private Long categoryId;
    private String description;

    public ProductDBO(Builder builder) {
        super(builder.id, builder.name);
        this.categoryId = builder.categoryId;
        this.description = builder.description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Builder {
        private final Long id;
        private String name;
        private Long categoryId;
        private String description;

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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public ProductDBO build() {
            return new ProductDBO(this);
        }
    }
}
