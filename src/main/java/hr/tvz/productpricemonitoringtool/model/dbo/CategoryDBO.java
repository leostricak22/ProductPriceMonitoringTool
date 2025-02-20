package hr.tvz.productpricemonitoringtool.model.dbo;

import hr.tvz.productpricemonitoringtool.model.Entity;

/**
 * Category database object.
 * Represents the category in the database.
 */
public class CategoryDBO extends Entity {

    private Long parentCategoryId;

    private CategoryDBO(Builder builder) {
        super(builder.id, builder.name);
        this.parentCategoryId = builder.parentCategoryId;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public static class Builder {
        private final Long id;
        private String name;
        private Long parentCategoryId;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parentCategoryId(Long parentCategoryId) {
            this.parentCategoryId = parentCategoryId;
            return this;
        }

        public CategoryDBO build() {
            return new CategoryDBO(this);
        }
    }
}
