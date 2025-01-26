package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CategoryRepository extends AbstractRepository<Category> {

    @Override
    public Optional<Category> findById(Long id) throws RepositoryAccessException {
        String query = """
        SELECT id, name, parent_category_id FROM "category" WHERE id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             var stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(ObjectMapper.mapResultSetToCategory(resultSet));
            }

            return Optional.empty();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public Set<Category> findAll() throws RepositoryAccessException {
        Set<Category> categories = new HashSet<>();

        String query = """
        SELECT id, name, parent_category_id FROM "category";
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                Category category = ObjectMapper.mapResultSetToCategory(resultSet);
                categories.add(category);
            }

            return categories;
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public Set<Category> save(Set<Category> entities) throws RepositoryAccessException {
        return Set.of();
    }

    public String findCategoryHierarchy(Long categoryId) throws RepositoryAccessException {
        Set<Category> categories = findAll();

        Optional<Category> category = categories.stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst();

        if (category.isEmpty()) {
            return "Category with id " + categoryId + " does not exist.";
        }

        StringBuilder hierarchy = new StringBuilder();
        Category currentCategory = category.get();
        while (currentCategory.getParentCategory().isPresent()) {
            hierarchy.insert(0," -> " +  currentCategory.getName());

            currentCategory = currentCategory.getParentCategory().get();
        }

        hierarchy.insert(0, currentCategory.getName());

        return hierarchy.toString();
    }
}
