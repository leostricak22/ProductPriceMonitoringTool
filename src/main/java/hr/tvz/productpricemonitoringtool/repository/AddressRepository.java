package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AddressRepository extends AbstractRepository<Address> {

    @Override
    public synchronized Optional<Address> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
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
        SELECT id, longitude, latitude, road, house_number, city, town, village, country
        FROM "address" WHERE ID = ?;
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
            PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(ObjectMapper.mapResultSetToAddress(resultSet));
            }

            return Optional.empty();
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    @Override
    public Set<Address> findAll() throws RepositoryAccessException {
        return Set.of();
    }

    @Override
    public synchronized Set<Address> save(Set<Address> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
        while (Boolean.TRUE.equals(DatabaseUtil.isActiveConnectionWithDatabase())) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DatabaseConnectionActiveException(e);
            }
        }

        DatabaseUtil.setActiveConnectionWithDatabase(true);
        Set<Address> savedAddresses = new HashSet<>();
        String query = """
        INSERT INTO "address" (longitude, latitude, road, house_number, city, town, village, country)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Address address : entities) {
                stmt.setBigDecimal(1, address.getLongitude());
                stmt.setBigDecimal(2, address.getLatitude());
                stmt.setString(3, address.getRoad());
                stmt.setString(4, address.getHouseNumber());
                stmt.setString(5, address.getCity());
                stmt.setString(6, address.getTown());
                stmt.setString(7, address.getVillage());
                stmt.setString(8, address.getCountry());

                stmt.addBatch();
            }

            stmt.executeBatch();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            for (Address address : entities) {
                if (generatedKeys.next()) {
                    address.setId(generatedKeys.getLong(1));
                } else {
                    throw new RepositoryQueryException("Creating address failed, no ID obtained.");
                }
                savedAddresses.add(address);
            }

            return savedAddresses;
        } catch (IOException | SQLException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }
}
