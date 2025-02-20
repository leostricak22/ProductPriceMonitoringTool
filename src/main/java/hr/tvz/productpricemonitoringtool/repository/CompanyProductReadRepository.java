package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.enumeration.FindingObject;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CompanyProductReadRepository {
    public synchronized Set<CompanyProductDBO> findByDateAndCompanyId(LocalDateTime date, Long companyId)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query = """
            SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
            FROM "company_product"
            WHERE created_at > ? AND company_id = ?
            ORDER BY company_id, product_id, created_at DESC;""";
        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, date);
            stmt.setLong(2, companyId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
        return companyProductsDBO;
    }

    public synchronized Set<CompanyProduct> findByProductIdAndCompanyId(Long productId, Long companyId,
            CompanyProductRecordType recordType) throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();

        String query;
        if (recordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query = """
                SELECT id, company_id, product_id, price, created_at
                FROM "company_product" WHERE product_id = ? AND company_id = ?""";
        } else {
            query = """
                SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
                FROM "company_product" WHERE product_id = ? AND company_id = ?
                ORDER BY company_id, product_id, created_at DESC;""";
        }

        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, productId);
            stmt.setLong(2, companyId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
        return companyProductsDBO.stream()
                .map(companyProductDBO -> {
                    try {
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, FindingObject.PRODUCT.getValue());
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public synchronized Set<CompanyProduct> findByProductId(Long productId, CompanyProductRecordType recordType)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query;
        if (recordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query = """
                SELECT id, company_id, product_id, price, created_at
                FROM "company_product"
                WHERE product_id = ?""";
        } else {
            query = """
                SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
                FROM "company_product"
                WHERE product_id = ?
                ORDER BY company_id, product_id, created_at DESC;""";
        }
        Set<CompanyProductDBO> companyProductsDBO = idOnlyQuery(productId, query);
        return companyProductsDBO.stream()
                .map(companyProductDBO -> {
                    try {
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, "company");
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public synchronized Set<CompanyProduct> findByCompanyId(Long companyId, CompanyProductRecordType recordType)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query;
        if (recordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query="SELECT id, company_id, product_id, price, created_at FROM \"company_product\" WHERE company_id = ?";
        } else {
            query = """
                SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
                FROM "company_product" WHERE company_id = ? ORDER BY company_id, product_id, created_at DESC;""";
        }
        Set<CompanyProductDBO> companyProductsDBO = idOnlyQuery(companyId, query);
        return companyProductsDBO.stream()
                .map(companyProductDBO -> {
                    try {
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, "product");
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public synchronized Set<CompanyProduct> findAll(CompanyProductRecordType recordType)
            throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        String query;
        if (recordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query = "SELECT id, company_id, product_id, price, created_at FROM \"company_product\"" ;
        } else {
            query = """
                SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
                FROM "company_product" ORDER BY company_id, product_id, created_at DESC;""";
        }
        Set<CompanyProductDBO> companyProductsDBO = companyProductDBOQuery(query);
        return companyProductsDBO.stream()
                .map(companyProductDBO -> {
                    try {
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, "product");
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    private synchronized Set<CompanyProductDBO> companyProductDBOQuery(String query) {
        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
        return companyProductsDBO;
    }

    public synchronized Set<CompanyProductDBO> findAllDBO() throws DatabaseConnectionActiveException {
        waitForDatabaseConnectionReady();
        return companyProductDBOQuery("SELECT id, company_id, product_id, price, created_at FROM \"company_product\";");
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

    private synchronized Set<CompanyProductDBO> idOnlyQuery(Long id, String query) {
        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();
        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
        return companyProductsDBO;
    }
}
