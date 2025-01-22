package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class AddressRepository extends AbstractRepository<Address> {

    @Override
    public Address findById(Long id) throws RepositoryAccessException {
        String query = """
        SELECT id, street, city, postal_code, country, house_number
        FROM "address" WHERE ID = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return ObjectMapper.mapResultSetToAddress(resultSet);
            } else {
                throw new RepositoryAccessException("Address with id " + id + " not found");
            }
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public Set<Address> findAll() throws RepositoryAccessException {
        return Set.of();
    }

    @Override
    public void save(Set<Address> entities) throws RepositoryAccessException {

    }
}
