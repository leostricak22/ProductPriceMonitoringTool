package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CompanyRepository extends AbstractRepository<Company> {

    @Override
    public Optional<Company> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
        return findAll().stream()
                .filter(company -> company.getId().equals(id))
                .findFirst();
    }

    @Override
    public synchronized Set<Company> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        SELECT id, name, address_id, join_code FROM "company";
        """;

        Set<CompanyDBO> companiesDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                companiesDBO.add(ObjectMapper.mapResultSetToCompanyDBO(resultSet));
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return ObjectMapper.mapCompanyDBOToCompany(companiesDBO);
    }

    public synchronized Set<Company> findAllByUserId(Long userId) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        SELECT c.id, c.name, c.address_id, c.join_code
        FROM "company" c
        JOIN "user_company" uc ON c.id = uc.company_id
        WHERE uc.user_id = ?;
        """;

        Set<CompanyDBO> companiesDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                companiesDBO.add(ObjectMapper.mapResultSetToCompanyDBO(resultSet));
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return ObjectMapper.mapCompanyDBOToCompany(companiesDBO);
    }

    @Override
    public synchronized Set<Company> save(Set<Company> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        INSERT INTO "company" (name, address_id, join_code)
        VALUES (?, ?, ?)
        """;

        Set<Company> savedCompanies = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Company company : entities) {
                stmt.setString(1, company.getName());
                stmt.setLong(2, company.getAddress().getId());
                stmt.setString(3, company.getJoinCode());

                stmt.addBatch();
            }

            stmt.executeBatch();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            for (Company company : entities) {
                if (generatedKeys.next()) {
                    company.setId(generatedKeys.getLong(1));
                    savedCompanies.add(company);
                }
            }

        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }

        return savedCompanies;
    }

    public synchronized void update(Company company) throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        UPDATE "company" SET name = ?, address_id = ?
        WHERE id = ?
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, company.getName());
            stmt.setLong(2, company.getAddress().getId());
            stmt.setLong(3, company.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized void delete(Company company) throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        DELETE FROM "company"
        WHERE id = ?
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, company.getId());

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
