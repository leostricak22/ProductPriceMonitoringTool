package hr.tvz.productpricemonitoringtool.thread;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FetchProductsByCategoriesThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(FetchProductsByCategoriesThread.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final List<Product> products;
    private final List<Category> categories;
    private final Optional<Category> parentCategory;
    private String hierarchy;
    private Boolean success;

    public FetchProductsByCategoriesThread(ProductRepository productRepository, CategoryRepository categoryRepository, List<Product> products, List<Category> categories, Optional<Category> parentCategory, String hierarchy, Boolean success) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.products = products;
        this.categories = categories;
        this.parentCategory = parentCategory;
        this.hierarchy = hierarchy;
        this.success = success;
    }

    @Override
    public void run() {
        try {
            products.addAll(productRepository.findAllByCategory(parentCategory));
            categories.addAll(categoryRepository.findAllByParentCategory(parentCategory));
            products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
            categories.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER));

            parentCategory.ifPresentOrElse(
                category -> {
                    try {
                        hierarchy = categoryRepository.findCategoryHierarchy(category.getId());
                    } catch (DatabaseConnectionActiveException e) {
                        logger.error("Error while fetching category hierarchy from database", e);
                    }
                },
                () -> hierarchy = ""
            );

            success = true;
        } catch (DatabaseConnectionActiveException e) {
            logger.error("Error while fetching products from database", e);
            products.clear();
            categories.clear();
            success = false;
        }
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getHierarchy() {
        return hierarchy;
    }
}
