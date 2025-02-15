package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.model.dbo.CompanyProductDBO;
import hr.tvz.productpricemonitoringtool.util.DatabaseUtil;
import hr.tvz.productpricemonitoringtool.util.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public synchronized Set<CompanyProduct> findByProductId(Long productId) throws DatabaseConnectionActiveException {
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
        SELECT id, company_id, product_id, price, created_at FROM "company_product" WHERE product_id=?;
        """;

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
                        return ObjectMapper.mapCompanyProductDBOToCompanyProduct(companyProductDBO);
                    } catch (DatabaseConnectionActiveException e) {
                        throw new RepositoryAccessException(e);
                    }
                })
                .collect(Collectors.toSet());
    }
}
