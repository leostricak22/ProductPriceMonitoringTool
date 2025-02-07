package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.ProductDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProductRepository extends AbstractRepository<Product> {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    @Override
    public Optional<Product> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
        return Optional.empty();
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
        SELECT id, name, category_id FROM "product";
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
        SELECT p.id, p.name, p.category_id
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
    public Set<Product> save(Set<Product> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
        return Set.of();
    }
}
