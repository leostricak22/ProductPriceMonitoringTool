package hr.tvz.productpricemonitoringtool.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Category model.
 * Represents the category in the model.
 */
public class Category extends Entity {

    private transient Optional<Category> parentCategory;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
