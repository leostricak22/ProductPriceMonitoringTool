package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * AddressRepository class.
 * Repository class for Address.
 * Contains methods for finding by id, finding all, saving, updating and deleting addresses.
 */
public class AddressRepository extends AbstractRepository<Address> {

    private static final Logger log = LoggerFactory.getLogger(AddressRepository.class);

    /**
     * Find address by id.
     * @param id Address id.
     */
    @Override
    public synchronized Optional<Address> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

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
            log.error("Failed to find address by id: {}", id, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    /**
     * Find all addresses.
     */
    @Override
    public synchronized Set<Address> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        Set<Address> addresses = new HashSet<>();

        String query = """
        SELECT id, longitude, latitude, road, house_number, city, town, village, country
        FROM "address";
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
            PreparedStatement stmt = connection.prepareStatement(query)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                addresses.add(ObjectMapper.mapResultSetToAddress(resultSet));
            }

            return addresses;
        } catch (IOException | SQLException e) {
            log.error("Failed to find all addresses.", e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    /**
     * Save addresses.
     * @param entities Set of addresses.
     */
    @Override
    public synchronized Set<Address> save(Set<Address> entities) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        Set<Address> savedAddresses = new HashSet<>();
        String query = """
        INSERT INTO "address" (longitude, latitude, road, house_number, city, town, village, country)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Address address : entities) {
                saveAddressToBatch(stmt, address);
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
            log.error("Failed to save addresses.", e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    /**
     * Update address.
     * @param address Address.
     */
    public synchronized void update(Address address) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        UPDATE "address" SET longitude = ?, latitude = ?, road = ?, house_number = ?, city = ?, town = ?, village = ?, country = ?
        WHERE id = ?
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
            PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setBigDecimal(1, address.getLongitude());
            stmt.setBigDecimal(2, address.getLatitude());
            stmt.setString(3, isNull(address.getRoad()) ? "?" : address.getRoad());
            stmt.setString(4, isNull(address.getHouseNumber()) ? "?" : address.getHouseNumber());
            stmt.setString(5, isNull(address.getCity()) ? "?" : address.getCity());
            stmt.setString(6, isNull(address.getTown()) ? "?" : address.getTown());
            stmt.setString(7, isNull(address.getVillage()) ? "?" : address.getVillage());
            stmt.setString(8, isNull(address.getCountry()) ? "?" : address.getCountry());
            stmt.setLong(9, address.getId());

            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            log.error("Failed to update address: {}", address, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    /**
     * Delete address.
     * @param address Address.
     */
    public synchronized void delete(Address address) throws RepositoryAccessException, DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query = """
        DELETE FROM "address" WHERE id = ?
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
            PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setLong(1, address.getId());
            stmt.executeUpdate();
        } catch (IOException | SQLException e) {
            log.error("Failed to delete address: {}", address, e);
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    /**
     * Wait for database connection to be ready.
     * @throws DatabaseConnectionActiveException Database connection active exception.
     */
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

    /**
     * Save address to batch.
     * @param stmt PreparedStatement.
     * @param address Address.
     */
    private void saveAddressToBatch(PreparedStatement stmt, Address address) throws SQLException {
        stmt.setBigDecimal(1, address.getLongitude());
        stmt.setBigDecimal(2, address.getLatitude());
        stmt.setString(3, isNull(address.getRoad()) ? "?" : address.getRoad());
        stmt.setString(4, isNull(address.getHouseNumber()) ? "?" : address.getHouseNumber());
        stmt.setString(5, isNull(address.getCity()) ? "?" : address.getCity());
        stmt.setString(6, isNull(address.getTown()) ? "?" : address.getTown());
        stmt.setString(7, isNull(address.getVillage()) ? "?" : address.getVillage());
        stmt.setString(8, isNull(address.getCountry()) ? "?" : address.getCountry());
        stmt.addBatch();
    }
}
