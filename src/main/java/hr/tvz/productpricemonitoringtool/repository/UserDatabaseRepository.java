package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDatabaseRepository extends AbstractRepository<User> {

    @Override
    public Optional<User> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Set<User> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException {
        Set<User> users = new HashSet<>();
        String query = """
        SELECT id, name, email, password, role FROM "user";
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                User user = ObjectMapper.mapResultSetToUser(resultSet);
                users.add(user);
            }

            return users;
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public Set<User> save(Set<User> entities) throws RepositoryAccessException {
        return Collections.emptySet();
    }

    public void update(User user) {
        String query = """
        UPDATE "user" SET name = ? WHERE id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setLong(2, user.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public void updatePassword(User user) {
        String query = """
        UPDATE "user" SET password = ? WHERE id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getPassword());
            stmt.setLong(2, user.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Optional<User> findByEmailAndPassword(String email, String password) throws DatabaseConnectionActiveException {
        String query = """
        SELECT id, name, email, password, role FROM "user" WHERE email = ? AND password = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                return Optional.of(ObjectMapper.mapResultSetToUser(resultSet));
            }

            return Optional.empty();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }
}