package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;

import java.io.IOException;
import java.sql.*;

public class CompanyProductWriteRepository {

    public static final String COMPANY_PRODUCT_INSERT_QUERY = """
            INSERT INTO "company_product" (company_id, product_id, price) VALUES (?, ?, ?);
        """;

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

    public synchronized void saveProductToCompanies(Product product)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query = COMPANY_PRODUCT_INSERT_QUERY;
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            for (CompanyProduct companyProduct : product.getCompanyProducts()) {
                stmt.setLong(1, companyProduct.getCompany().getId());
                stmt.setLong(2, product.getId());
                stmt.setBigDecimal(3, companyProduct.getPrice().value());
                stmt.addBatch();
            }
            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized void updatePrice(Long companyId, Long productId, Price price)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query = COMPANY_PRODUCT_INSERT_QUERY;
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, companyId);
            stmt.setLong(2, productId);
            stmt.setBigDecimal(3, price.value());
            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized CompanyProduct save(CompanyProduct companyProduct)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query = COMPANY_PRODUCT_INSERT_QUERY;
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, companyProduct.getCompany().getId());
            stmt.setLong(2, companyProduct.getProduct().getId());
            stmt.setBigDecimal(3, companyProduct.getPrice().value());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    companyProduct.setId(generatedKeys.getLong(1));
                } else {
                    throw new RepositoryQueryException("Creating company product failed, no ID obtained.");
                }
            }
            return companyProduct;
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized void update(CompanyProductDBO companyProduct)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query = """
            UPDATE "company_product"
            SET company_id = ?, product_id = ?, price = ?
            WHERE id = ?;
        """;
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, companyProduct.getCompanyId());
            stmt.setLong(2, companyProduct.getProductId());
            stmt.setBigDecimal(3, companyProduct.getPrice().value());
            stmt.setLong(4, companyProduct.getId());
            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized void delete(Long id) throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query = """
            DELETE FROM "company_product"
            WHERE id = ?;
        """;
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }
}
