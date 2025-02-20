package hr.tvz.productpricemonitoringtool.model;

import hr.tvz.productpricemonitoringtool.util.Constants;
import hr.tvz.productpricemonitoringtool.util.FileUtil;
import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * Product model.
 * Represents the product in the model.
 */
public class Product extends Entity implements Serializable {

    private Category category;
    private Set<CompanyProduct> companyProducts;
    private String description;

    public Product(Builder builder) {
        super(builder.id, builder.name);
        this.category = builder.category;
        this.companyProducts = builder.companyProducts;
        this.description = builder.description;
    }

    public Product(Product product) {
        super(product.getId(), product.getName());
        this.category = product.getCategory();
        this.companyProducts = product.getCompanyProducts();
        this.description = product.getDescription();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<CompanyProduct> getCompanyProducts() {
        return companyProducts;
    }

    public void setCompanyProducts(Set<CompanyProduct> companyProducts) {
        this.companyProducts = companyProducts;
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
        private Category category;
        private Set<CompanyProduct> companyProducts;
        private String description;

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

        public Builder companyProducts(Set<CompanyProduct> companyProducts) {
            this.companyProducts = companyProducts;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    /**
     * Get image of the product.
     * @return Image of the product.
     * If the image does not exist, return the default image.
     */
    public Image getImage() {
        for (String extension : Constants.IMAGE_EXTENSIONS) {
            String filePath = "files/product/" + super.id + extension.substring(1);
            File productImageFile = new File(filePath);

            if (!productImageFile.exists())
                continue;

            return FileUtil.cropImageToSquare(new Image(productImageFile.toURI().toString()));
        }

        return FileUtil.cropImageToSquare(
                new Image(Constants.NO_IMAGE_URL));
    }

    @Override
    public String toString() {
        return "Product{" +
                "category=" + category.getName() +
                ", companyProducts=" + (!isNull(companyProducts) ? companyProducts.size() : '0') +
                ", description='" + description + '\'' +
                '}';
    }
}
