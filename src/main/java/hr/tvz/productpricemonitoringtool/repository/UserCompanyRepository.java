package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.model.dbo.UserCompanyDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserCompanyRepository {

    public synchronized void addUser(Long userId, Long companyId) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        INSERT INTO "user_company" (user_id, company_id)
        VALUES (?, ?)
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, companyId);

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized Set<User> findAllUsers(Company company) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        SELECT u.id, u.name, u.email, u.role, u.password
        FROM "user" u
        JOIN "user_company" uc ON u.id = uc.user_id
        WHERE uc.company_id = ?
        """;

        Set<User> users = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, company.getId());
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                users.add(ObjectMapper.mapResultSetToUserWithoutCompanies(resultSet));
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return users;
    }

    public synchronized Set<UserCompanyDBO> findAllUserCompanyDBO() throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        SELECT uc.id, uc.user_id, uc.company_id, uc.created_at
        FROM "user_company" uc
        """;

        Set<UserCompanyDBO> userCompanyDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                userCompanyDBO.add(ObjectMapper.mapResultSetToUserCompanyDBO(resultSet));
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return userCompanyDBO;
    }

    public synchronized Set<UserCompanyDBO> findAllUserCompanyByUserId(LocalDateTime date, Long userId) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        SELECT uc.id, uc.user_id, uc.company_id, uc.created_at
        FROM "user_company" uc
        WHERE uc.user_id = ?
        AND uc.created_at > ?
        """;

        Set<UserCompanyDBO> userCompanyDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(date));
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                userCompanyDBO.add(ObjectMapper.mapResultSetToUserCompanyDBO(resultSet));
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return userCompanyDBO;
    }

    public synchronized UserCompanyDBO updateUserCompany(UserCompanyDBO userCompany) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        UPDATE "user_company" SET user_id = ?, company_id = ?
        WHERE id = ?
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userCompany.getUserId());
            stmt.setLong(2, userCompany.getCompanyId());
            stmt.setLong(3, userCompany.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return userCompany;
    }

    public synchronized void deleteUserCompany(UserCompanyDBO userCompany) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        DELETE FROM "user_company"
        WHERE id = ?
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userCompany.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    private synchronized void waitForDatabaseConnectionReady() throws DatabaseConnectionActiveException {
        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);
    }
}
