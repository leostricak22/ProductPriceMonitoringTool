package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.dbo.CategoryDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * CategoryRepository class.
 * Repository class for Category.
 * Contains methods for finding by id, finding all, saving, updating and deleting categories.
 */
public class CategoryRepository extends AbstractRepository<Category> {

    private static final Logger log = LoggerFactory.getLogger(CategoryRepository.class);

    /**
     * Find category by id.
     * @param id Category id.
     */
    @Override
    public synchronized Optional<Category> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = "SELECT id, name, parent_category_id FROM \"category\" WHERE id = ?;" ;

        Optional<CategoryDBO> categoryDBO = Optional.empty();
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                categoryDBO = Optional.of(ObjectMapper.mapResultSetToCategoryDBO(resultSet));
            }
        } catch (IOException | SQLException e) {
            log.error("Failed to find category by id: {}", id, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        if (categoryDBO.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ObjectMapper.mapCategoryDBOToCategory(categoryDBO.get()));
    }

    /**
     * Find all categories.
     */
    @Override
    public synchronized Set<Category> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        Set<CategoryDBO> categoriesDBO = new HashSet<>();
        String query = "SELECT id, name, parent_category_id FROM \"category\";" ;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                categoriesDBO.add(ObjectMapper.mapResultSetToCategoryDBO(resultSet));
            }
        } catch (IOException | SQLException e) {
            log.error("Failed to find all categories.", e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return new HashSet<>(ObjectMapper.mapCategoryDBOToCategory(categoriesDBO));
    }

    /**
     * Save categories.
     * @param entities Categories to save.
     */
    @Override
    public synchronized Set<Category> save(Set<Category> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = "INSERT INTO \"category\" (name, parent_category_id) VALUES (?, ?);" ;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (Category category : entities) {
                stmt.setString(1, category.getName());
                stmt.setObject(2, category.getParentCategory().map(Category::getId).orElse(null));
                stmt.addBatch();
            }

            stmt.executeBatch();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            for (Category category : entities) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getLong(1));
                }
            }
        } catch (IOException | SQLException e) {
            log.error("Failed to save categories.", e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return entities;
    }

    /**
     * Find all categories by parent category.
     * @param category Parent category.
     */
    public List<Category> findAllByParentCategory(Optional<Category> category) throws RepositoryAccessException, DatabaseConnectionActiveException {
        List<Category> categories = new ArrayList<>(findAll());

        return category.map(value -> categories.stream()
                .filter(c -> c.getParentCategory().isPresent() &&
                        c.getParentCategory().get().getId().equals(value.getId()))
                .toList()).orElseGet(() -> categories.stream()
                .filter(c -> c.getParentCategory().isEmpty())
                .toList());
    }

    /**
     * Find all categories by parent category recursively.
     * @param category Parent category.
     */
    public List<Category> findAllByParentCategoryRecursively(Optional<Category> category) throws RepositoryAccessException, DatabaseConnectionActiveException {
        List<Category> categories = new ArrayList<>(findAll());
        if (category.isEmpty()) {
            return categories;
        }

        List<Category> allSubCategories = new ArrayList<>();
        allSubCategories.add(category.get());
        collectSubcategories(category.get(), categories, allSubCategories);
        return allSubCategories;
    }

    /**
     * Collect subcategories recursively.
     * @param parent Parent category.
     */
    private void collectSubcategories(Category parent, List<Category> categories, List<Category> allSubCategories) {
        List<Category> subCategories = categories.stream()
                .filter(c -> c.getParentCategory().isPresent() && c.getParentCategory().get().getId().equals(parent.getId()))
                .toList();

        allSubCategories.addAll(subCategories);

        for (Category subCategory : subCategories) {
            collectSubcategories(subCategory, categories, allSubCategories);
        }
    }

    /**
     * Find category hierarchy.
     * @param categoryId Category id.
     */
    public String findCategoryHierarchy(Long categoryId) throws RepositoryAccessException, DatabaseConnectionActiveException {
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

    /**
     * Update category.
     * @param category Category to update.
     */
    public synchronized void update(Category category) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        UPDATE "category" SET name = ?, parent_category_id = ? WHERE id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category.getName());
            stmt.setObject(2, category.getParentCategory().map(Category::getId).orElse(null));
            stmt.setLong(3, category.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            log.error("Failed to update category: {}", category, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    /**
     * Delete category.
     * @param category Category to delete.
     */
    public synchronized void delete(Category category) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        DELETE FROM "category" WHERE id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, category.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            log.error("Failed to delete category: {}", category, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    /**
     * Wait for database connection ready.
     * @throws DatabaseConnectionActiveException Database connection active exception.
     */
    private synchronized void waitForDatabaseConnectionReady() throws DatabaseConnectionActiveException {
        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error("Failed to wait for database connection ready.", e);
                Thread.currentThread().interrupt();
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);
    }
}
