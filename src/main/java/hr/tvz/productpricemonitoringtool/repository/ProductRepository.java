package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.enumeration.LogChangeField;
import hr.tvz.productpricemonitoringtool.exception.AuthenticationException;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.AuditLogManager;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.ProductDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;
import hr.tvz.productpricemonitoringtool.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * ProductRepository class.
 * Repository class for Product.
 * Contains methods for finding by id, finding all, finding all by category, saving, updating and deleting products.
 */
public class ProductRepository extends AbstractRepository<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final CompanyProductWriteRepository companyProductWriteRepository = new CompanyProductWriteRepository();

    /**
     * Find product by id.
     * @param id Product id.
     */
    @Override
    public Optional<Product> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
        return findAll().stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    /**
     * Find product by id without companies.
     * @param id Product id.
     */
    public Optional<Product> findByIdWithoutCompanies(Long id) throws DatabaseConnectionActiveException {
        Optional<Product> product = findById(id);
        product.ifPresent(value -> value.setCompanyProducts(new HashSet<>()));

        return product;
    }

    /**
     * Find all products.
     */
    @Override
    public synchronized Set<Product> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = "SELECT id, name, category_id, description FROM \"product\";" ;

        Set<ProductDBO> productsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                productsDBO.add(ObjectMapper.mapResultSetToProductDBO(resultSet));
            }
        } catch (SQLException | IOException e) {
            log.error("Failed to find all products.", e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return ObjectMapper.mapProductDBOToProduct(productsDBO);
    }

    /**
     * Find all products by category.
     * @param category Category.
     */
    public synchronized Set<Product> findAllByCategory(Optional<Category> category) throws RepositoryAccessException, DatabaseConnectionActiveException {
        List<Category> allCategories = categoryRepository.findAllByParentCategoryRecursively(category);
        waitForDatabaseConnectionReady();

        String query = """
        SELECT p.id, p.name, p.category_id, p.description
        FROM "product" p
        WHERE p.category_id = ?;
        """;

        Set<ProductDBO> productsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query);) {

            for (Category c : allCategories) {
                stmt.setLong(1, c.getId());
                try (ResultSet resultSet = stmt.executeQuery()) {
                    while (resultSet.next()) {
                        productsDBO.add(ObjectMapper.mapResultSetToProductDBO(resultSet));
                    }
                }
            }
        } catch (SQLException | IOException e) {
            log.error("Failed to find all products by category: {}", category, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return ObjectMapper.mapProductDBOToProduct(productsDBO);
    }

    /**
     * Save product.
     * @param entities Products.
     */
    @Override
    public synchronized Set<Product> save(Set<Product> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = "INSERT INTO \"product\" (name, category_id, description) VALUES (?, ?, ?);" ;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Product p : entities) {
                stmt.setString(1, p.getName());
                stmt.setLong(2, p.getCategory().getId());
                stmt.setString(3, p.getDescription());
                stmt.addBatch();
            }

            stmt.executeBatch();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                for (Product p : entities) {
                    if (generatedKeys.next()) {
                        p.setId(generatedKeys.getLong(1));
                    } else {
                        throw new RepositoryAccessException("Creating product failed, no ID obtained.");
                    }
                }
            }

        } catch (SQLException | IOException e) {
            log.error("Failed to save products: {}", entities, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        for (Product product : entities) {
            AuditLogManager.logChange(LogChangeField.PRODUCT.toString(), "-", product,
                    Session.getLoggedInUser().orElseThrow(
                            () -> new AuthenticationException("User not logged in")));
            if (!isNull(product.getCompanyProducts())) {
                companyProductWriteRepository.saveProductToCompanies(product);
            }
        }

        return entities;
    }

    /**
     * Update product.
     * @param product Product.
     */
    public synchronized void update(Product product) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = "UPDATE \"product\" SET name = ?, category_id = ?, description = ? WHERE id = ?;" ;

        Product oldProduct = new Product(product);

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setLong(2, product.getCategory().getId());
            stmt.setString(3, product.getDescription());
            stmt.setLong(4, product.getId());

            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            log.error("Failed to update product: {}", product, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        AuditLogManager.logChange(LogChangeField.PRODUCT.toString(), oldProduct, product.toString(),
                Session.getLoggedInUser().orElseThrow(
                        () -> new AuthenticationException("User not loggedin")));

        if (!isNull(product.getCompanyProducts()) && !product.getCompanyProducts().isEmpty()) {
            companyProductWriteRepository.saveProductToCompanies(product);
        }
    }

    /**
     * Delete product.
     * @param product Product.
     */
    public synchronized void delete(Product product) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = "DELETE FROM \"product\" WHERE id = ?;" ;

        Product oldProduct = new Product(product);

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, product.getId());

            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            log.error("Failed to delete product: {}", product, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        AuditLogManager.logChange(LogChangeField.PRODUCT.toString(), oldProduct, "-",
                Session.getLoggedInUser().orElseThrow(
                        () -> new AuthenticationException("User not logged in")));
    }

    /**
     * Wait for database connection ready.
     */
    private synchronized void waitForDatabaseConnectionReady() throws DatabaseConnectionActiveException {
        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Failed to wait for database connection to be ready.", e);
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);
    }
}
