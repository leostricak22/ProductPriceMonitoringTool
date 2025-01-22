package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CompanyRepository extends AbstractRepository<Company> {

    @Override
    public Company findById(Long id) throws RepositoryAccessException {
        return null;
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
    public void save(Set<Company> entities) throws RepositoryAccessException {
        // Save companies
    }
}
