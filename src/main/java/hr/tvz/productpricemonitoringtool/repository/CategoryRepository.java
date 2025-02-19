package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.dbo.CategoryDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class CategoryRepository extends AbstractRepository<Category> {

    @Override
    public synchronized Optional<Category> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
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
        SELECT id, name, parent_category_id FROM "category" WHERE id = ?;
        """;

        Optional<CategoryDBO> categoryDBO = Optional.empty();
        try (Connection connection = DatabaseUtil.connectToDatabase();
             var stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                categoryDBO = Optional.of(ObjectMapper.mapResultSetToCategoryDBO(resultSet));
            }
        } catch (IOException | SQLException e) {
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

    @Override
    public synchronized Set<Category> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException {
        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);

        Set<CategoryDBO> categoriesDBO = new HashSet<>();

        String query = """
        SELECT id, name, parent_category_id FROM "category";
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                categoriesDBO.add(ObjectMapper.mapResultSetToCategoryDBO(resultSet));
            }
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return new HashSet<>(ObjectMapper.mapCategoryDBOToCategory(categoriesDBO));
    }

    @Override
    public synchronized Set<Category> save(Set<Category> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
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
        INSERT INTO "category" (name, parent_category_id) VALUES (?, ?)
        """;

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
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return entities;
    }

    public List<Category> findAllByParentCategory(Optional<Category> category) throws RepositoryAccessException, DatabaseConnectionActiveException {
        List<Category> categories = new ArrayList<>(findAll());

        return category.map(value -> categories.stream()
                .filter(c -> c.getParentCategory().isPresent() &&
                        c.getParentCategory().get().getId().equals(value.getId()))
                .toList()).orElseGet(() -> categories.stream()
                .filter(c -> c.getParentCategory().isEmpty())
                .toList());
    }

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

    private void collectSubcategories(Category parent, List<Category> categories, List<Category> allSubCategories) {
        List<Category> subCategories = categories.stream()
                .filter(c -> c.getParentCategory().isPresent() && c.getParentCategory().get().getId().equals(parent.getId()))
                .toList();

        allSubCategories.addAll(subCategories);

        for (Category subCategory : subCategories) {
            collectSubcategories(subCategory, categories, allSubCategories);
        }
    }

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

    public synchronized void update(Category category) throws RepositoryAccessException, DatabaseConnectionActiveException {
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
        UPDATE "category" SET name = ?, parent_category_id = ? WHERE id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category.getName());
            stmt.setObject(2, category.getParentCategory().map(Category::getId).orElse(null));
            stmt.setLong(3, category.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized void delete(Category category) throws RepositoryAccessException, DatabaseConnectionActiveException {
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
        DELETE FROM "category" WHERE id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, category.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }
}
