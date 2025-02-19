package hr.tvz.productpricemonitoringtool.model;

import java.io.Serializable;
import java.util.Optional;

public class Category extends Entity implements Serializable {

    private Optional<Category> parentCategory;

    private Category(Builder builder) {
        super(builder.id, builder.name);
        this.parentCategory = builder.parentCategory;
    }

    public Optional<Category> getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = Optional.of(parentCategory);
    }

    public static class Builder {
        private final Long id;
        private String name;
        private Optional<Category> parentCategory;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parentCategory(Optional<Category> parentCategory) {
            this.parentCategory = parentCategory;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }
}
