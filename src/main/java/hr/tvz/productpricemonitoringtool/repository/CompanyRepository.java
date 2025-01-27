package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CompanyRepository extends AbstractRepository<Company> {

    @Override
    public Optional<Company> findById(Long id) throws RepositoryAccessException {
        return Optional.empty();
    }

    @Override
    public Set<Company> findAll() throws RepositoryAccessException {
        Set<Company> companies = new HashSet<>();
        String query = """
        SELECT id, name, address_id FROM "company";
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                Company company = ObjectMapper.mapResultSetToCompany(resultSet);
                companies.add(company);
            }

            return companies;
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Set<Company> findAllByUserId(Long userId) throws RepositoryAccessException {
        Set<Company> companies = new HashSet<>();
        String query = """
        SELECT c.id, c.name, c.address_id
        FROM "company" c
        JOIN "user_company" uc ON c.id = uc.company_id
        WHERE uc.user_id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Company company = ObjectMapper.mapResultSetToCompany(resultSet);
                companies.add(company);
            }

            return companies;
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public Set<Company> save(Set<Company> entities) throws RepositoryAccessException {
        Set<Company> savedCompanies = new HashSet<>();
        String query = """
        INSERT INTO "company" (name, address_id)
        VALUES (?, ?)
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Company company : entities) {
                stmt.setString(1, company.getName());
                stmt.setLong(2, company.getAddress().getId());

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

            return savedCompanies;
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public Set<Company> findAllByProductId(Long productId) throws RepositoryAccessException {
        Set<Company> companies = new HashSet<>();
        String query = """
        SELECT c.id, c.name, c.address_id
        FROM "company" c
        JOIN "company_product" cp ON c.id = cp.company_id
        WHERE cp.product_id = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, productId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Company company = ObjectMapper.mapResultSetToCompany(resultSet);
                companies.add(company);
            }

            return companies;
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    public void addUser(Long userId, Long companyId) throws RepositoryAccessException {
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
        }
    }
}
