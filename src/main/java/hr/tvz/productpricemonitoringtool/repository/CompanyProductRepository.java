package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.Product;
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

public class CompanyProductRepository {

    public synchronized void saveProductToCompanies(Product product) throws DatabaseConnectionActiveException {
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
        INSERT INTO "company_product" (company_id, product_id, price) VALUES (?, ?, ?);
        """;

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

    public synchronized Set<CompanyProductDBO> findByDateAndCompanyId(LocalDateTime date,Long companyId) throws DatabaseConnectionActiveException {
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
            SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
            FROM "company_product"
            WHERE created_at > ? AND company_id = ?
            ORDER BY company_id, product_id, created_at DESC;
        """;

        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, date);
            stmt.setLong(2, companyId);

            ResultSet resultSet = stmt.executeQuery();

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

    public synchronized Set<CompanyProduct> findByProductIdAndCompanyId(
            Long productId,
            Long companyId,
            CompanyProductRecordType companyProductRecordType) throws DatabaseConnectionActiveException {
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
            SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
            FROM "company_product"
            WHERE product_id = ? AND company_id = ?
            ORDER BY company_id, product_id, created_at DESC;
        """;

        if (companyProductRecordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query = """
                SELECT id, company_id, product_id, price, created_at
                FROM "company_product"
                WHERE product_id = ? AND company_id = ?
            """;
        }

        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, productId);
            stmt.setLong(2, companyId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
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
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, "product");
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public synchronized Set<CompanyProduct> findByProductId(
            Long productId,
            CompanyProductRecordType companyProductRecordType) throws DatabaseConnectionActiveException {
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
            SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
            FROM "company_product"
            WHERE product_id = ?
            ORDER BY company_id, product_id, created_at DESC;
        """;

        if (companyProductRecordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query = """
                SELECT id, company_id, product_id, price, created_at
                FROM "company_product"
                WHERE product_id = ?
            """;
        }

        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, productId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
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
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, "company");
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public synchronized Set<CompanyProduct> findByCompanyId(Long companyId, CompanyProductRecordType companyProductRecordType) throws DatabaseConnectionActiveException {
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
            SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
            FROM "company_product"
            WHERE company_id = ?
            ORDER BY company_id, product_id, created_at DESC;
        """;

        if (companyProductRecordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query = """
                SELECT id, company_id, product_id, price, created_at
                FROM "company_product"
                WHERE company_id = ?
            """;
        }

        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, companyId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
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
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, "product");
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public synchronized void updatePrice(Long companyId, Long productId, Price price) throws DatabaseConnectionActiveException {
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
        INSERT INTO "company_product" (company_id, product_id, price) VALUES (?, ?, ?);
        """;

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

    public synchronized Set<CompanyProduct> findAll(CompanyProductRecordType companyProductRecordType) throws DatabaseConnectionActiveException {
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
            SELECT DISTINCT ON (company_id, product_id) id, company_id, product_id, price, created_at
            FROM "company_product"
            ORDER BY company_id, product_id, created_at DESC;
        """;

        if (companyProductRecordType.equals(CompanyProductRecordType.ALL_RECORDS)) {
            query = """
                SELECT id, company_id, product_id, price, created_at
                FROM "company_product"
            """;
        }

        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                companyProductsDBO.add(ObjectMapper.mapResultSetToCompanyProductDBO(resultSet));
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
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO, "product");
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    public synchronized Set<CompanyProductDBO> findAllDBO() throws DatabaseConnectionActiveException {
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
        SELECT id, company_id, product_id, price, created_at
        FROM "company_product";
        """;

        Set<CompanyProductDBO> companyProductsDBO = new HashSet<>();

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();

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

    public synchronized CompanyProduct save(CompanyProduct companyProduct) throws DatabaseConnectionActiveException {
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
        INSERT INTO "company_product" (company_id, product_id, price) VALUES (?, ?, ?);
        """;

        try (Connection connection = DatabaseUtil.connectToDatabase();
             PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, companyProduct.getCompany().getId());
            stmt.setLong(2, companyProduct.getProduct().getId());
            stmt.setBigDecimal(3, companyProduct.getPrice().value());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                companyProduct.setId(generatedKeys.getLong(1));
            }

            return companyProduct;
        } catch (SQLException | IOException e) {
            throw new RepositoryAccessException(e);
        } finally {
            DatabaseUtil.setActiveConnectionWithDatabase(false);
            notifyAll();
        }
    }

    public synchronized void update(CompanyProductDBO companyProduct) throws DatabaseConnectionActiveException {
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
