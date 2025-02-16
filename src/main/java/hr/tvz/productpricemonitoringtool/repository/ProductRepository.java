package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.ProductDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProductRepository extends AbstractRepository<Product> {

    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final CompanyProductRepository companyProductRepository = new CompanyProductRepository();

    @Override
    public Optional<Product> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
        return findAll().stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    public Optional<Product> findByIdWithoutCompanies(Long id) throws DatabaseConnectionActiveException {
        Optional<Product> product = findById(id);
        product.ifPresent(value -> value.setCompanyProducts(new HashSet<>()));

        return product;
    }

    @Override
    public synchronized Set<Product> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException {
        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);

        String query = """
        SELECT id, name, category_id, description FROM "product";
        """;

        Set<ProductDBO> productsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                productsDBO.add(ObjectMapper.mapResultSetToProductDBO(resultSet));
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return ObjectMapper.mapProductDBOToProduct(productsDBO);
    }

    public synchronized Set<Product> findAllByCategory(Optional<Category> category) throws RepositoryAccessException, DatabaseConnectionActiveException {
        List<Category> allCategories = categoryRepository.findAllByParentCategoryRecursively(category);

        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);

        String query = """
        SELECT p.id, p.name, p.category_id, p.description
        FROM "product" p
        WHERE p.category_id = ?;
        """;

        Set<ProductDBO> productsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             var stmt = connection.prepareStatement(query)) {

            for (Category c : allCategories) {
                stmt.setLong(1, c.getId());
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    productsDBO.add(ObjectMapper.mapResultSetToProductDBO(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return ObjectMapper.mapProductDBOToProduct(productsDBO);
    }

    @Override
    public synchronized Set<Product> save(Set<Product> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);

        String query = """
        INSERT INTO "product" (name, category_id, description) VALUES (?, ?, ?);
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (Product p : entities) {
                stmt.setString(1, p.getName());
                stmt.setLong(2, p.getCategory().getId());
                stmt.setString(3, p.getDescription());

                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        p.setId(generatedKeys.getLong(1));
                    } else {
                        throw new RepositoryAccessException("Creating product failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        for (Product product : entities) {
            companyProductRepository.saveProductToCompanies(product);
        }

        return entities;
    }
}
