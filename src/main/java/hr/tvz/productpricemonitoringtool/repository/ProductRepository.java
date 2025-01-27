package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Product;
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
    public Optional<Product> findById(Long id) throws RepositoryAccessException {
        return Optional.empty();
    }

    @Override
    public Set<Product> findAll() throws RepositoryAccessException {
        Set<Product> products = new HashSet<>();
        String query = """
        SELECT id, name, category_id FROM "product";
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                Product product = ObjectMapper.mapResultSetToProduct(resultSet);
                products.add(product);
            }
            return products;
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Set<Product> findAllByCategory(Optional<Category> category) throws RepositoryAccessException {
        List<Category> allCategories = categoryRepository.findAllByParentCategoryRecursively(category);

        Set<Product> products = new HashSet<>();
        String query = """
        SELECT p.id, p.name, p.category_id
        FROM "product" p
        WHERE p.category_id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             var stmt = connection.prepareStatement(query)) {

            for (Category c : allCategories) {
                stmt.setLong(1, c.getId());
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    Product product = ObjectMapper.mapResultSetToProduct(resultSet);
                    products.add(product);
                }
            }

            return products;
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public Set<Product> save(Set<Product> entities) throws RepositoryAccessException {
        return Set.of();
    }
}
