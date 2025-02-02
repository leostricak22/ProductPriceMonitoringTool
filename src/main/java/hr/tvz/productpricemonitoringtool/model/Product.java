package hr.tvz.productpricemonitoringtool.model;

import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.FileUtil;
import javafx.scene.image.Image;

import java.io.File;
import java.util.Set;

public class Product extends Entity {

    private Category category;
    private Set<Company> companies;

    public Product(Builder builder) {
        super(builder.id, builder.name);
        this.category = builder.category;
        this.companies = builder.companies;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }

    public static class Builder {
        private final Long id;
        private String name;
        private Category category;
        private Set<Company> companies;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder companies(Set<Company> companies) {
            this.companies = companies;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    public Image getImage() {
        for (String extension : Constants.IMAGE_EXTENSIONS) {
            String filePath = "files/product/" + super.id + extension.substring(1);
            File productImageFile = new File(filePath);

            if (!productImageFile.exists())
                continue;

            return FileUtil.cropImageToSquare(new Image(productImageFile.toURI().toString()));
        }

        return FileUtil.cropImageToSquare(
                new Image("file:files/product/no-image.png"));
    }
}
