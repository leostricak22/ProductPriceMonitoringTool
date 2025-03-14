package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.*;
import hr.tvz.productpricemonitoringtool.model.dbo.*;
import hr.tvz.productpricemonitoringtool.repository.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ObjectMapper class.
 * Contains methods for mapping database objects to domain objects.
 */
public class ObjectMapper {

    private static final AddressRepository addressRepository = new AddressRepository();
    private static final CompanyRepository companyRepository = new CompanyRepository();
    private static final CategoryRepository categoryRepository = new CategoryRepository();
    private static final ProductRepository productRepository = new ProductRepository();
    private static final CompanyProductReadRepository companyProductReadRepository = new CompanyProductReadRepository();

    private ObjectMapper() {}

    /**
     * Method for mapping ResultSet to User.
     * @param resultSet ResultSet.
     */
    public static User mapResultSetToUser(ResultSet resultSet) throws SQLException, DatabaseConnectionActiveException {
        return new User.Builder(resultSet.getLong("id"), resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(Role.valueOf(resultSet.getString("role")))
                .companies(companyRepository.findAllByUserId(resultSet.getLong("id")))
                .build();
    }

    /**
     * Method for mapping ResultSet to User without companies.
     * @param resultSet ResultSet.
     */
    public static User mapResultSetToUserWithoutCompanies(ResultSet resultSet) throws SQLException {
        return new User.Builder(resultSet.getLong("id"), resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(Role.valueOf(resultSet.getString("role")))
                .build();
    }

    /**
     * Method for mapping CompanyDBO to Company.
     */
    public static Set<Company> mapCompanyDBOToCompany(Set<CompanyDBO> companyDBO) throws DatabaseConnectionActiveException {
        Set<Company> companies = new HashSet<>();
        for (CompanyDBO company : companyDBO) {
            companies.add(mapCompanyDBOToCompany(company));
        }
        return companies;
    }

    /**
     * Method for mapping CompanyDBO to Company.
     * @param companyDBO CompanyDBO.
     */
    public static Company mapCompanyDBOToCompany(CompanyDBO companyDBO) throws DatabaseConnectionActiveException {
        return new Company.Builder(companyDBO.getId(), companyDBO.getName())
                .address(addressRepository.findById(companyDBO.getAddressId())
                        .orElseThrow(() -> new RepositoryQueryException("Address not found")))
                .joinCode(companyDBO.getJoinCode())
                .build();
    }

    /**
     * Method for mapping ResultSet to CompanyDBO.
     * @param resultSet ResultSet.
     */
    public static CompanyDBO mapResultSetToCompanyDBO(ResultSet resultSet) throws SQLException {
        return new CompanyDBO.Builder(resultSet.getLong("id"), resultSet.getString("name"))
                .addressId(resultSet.getLong("address_id"))
                .joinCode(resultSet.getString("join_code"))
                .build();
    }

    /**
     * Method for mapping ResultSet to Address.
     * @param resultSet ResultSet.
     */
    public static Address mapResultSetToAddress(ResultSet resultSet) throws SQLException {
        return new Address.Builder(resultSet.getLong("id"))
                .longitude(resultSet.getBigDecimal("longitude"))
                .latitude(resultSet.getBigDecimal("latitude"))
                .road(resultSet.getString("road"))
                .houseNumber(resultSet.getString("house_number"))
                .city(resultSet.getString("city"))
                .town(resultSet.getString("town"))
                .village(resultSet.getString("village"))
                .country(resultSet.getString("country"))
                .build();
    }

    /**
     * Method for mapping CategoryDBO to Category.
     * @param categoryDBO CategoryDBO.
     */
    public static Category mapCategoryDBOToCategory(CategoryDBO categoryDBO) throws DatabaseConnectionActiveException {
        return new Category.Builder(categoryDBO.getId())
                .name(categoryDBO.getName())
                .parentCategory(categoryRepository.findById(categoryDBO.getParentCategoryId()))
                .build();
    }

    /**
     * Method for mapping CategoryDBO to Category.
     * @param categoryDBOSet CategoryDBO set.
     */
    public static List<Category> mapCategoryDBOToCategory(Set<CategoryDBO> categoryDBOSet) throws DatabaseConnectionActiveException {
        List<Category> categories = new ArrayList<>();
        for (CategoryDBO categoryDBO : categoryDBOSet) {
            categories.add(mapCategoryDBOToCategory(categoryDBO));
        }

        return categories;
    }

    /**
     * Method for mapping ResultSet to CategoryDBO.
     * @param resultSet ResultSet.
     */
    public static CategoryDBO mapResultSetToCategoryDBO(ResultSet resultSet) throws SQLException {
        return new CategoryDBO.Builder(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .parentCategoryId(resultSet.getLong("parent_category_id"))
                .build();
    }

    /**
     * Method for mapping ProductDBO to Product.
     * @param productDBO ProductDBO.
     */
    public static Product mapProductDBOToProduct(ProductDBO productDBO) throws DatabaseConnectionActiveException {
        return new Product.Builder(productDBO.getId())
                .name(productDBO.getName())
                .category(categoryRepository.findById(productDBO.getCategoryId())
                        .orElseThrow(() -> new RepositoryQueryException("Category not found")))
                .companyProducts(companyProductReadRepository.findByProductId(productDBO.getId(),
                        CompanyProductRecordType.LATEST_RECORD))
                .description(productDBO.getDescription())
                .build();
    }

    /**
     * Method for mapping ProductDBO to Product.
     * @param productDBOSet ProductDBO set.
     */
    public static Set<Product> mapProductDBOToProduct(Set<ProductDBO> productDBOSet) throws DatabaseConnectionActiveException {
        Set<Product> products = new HashSet<>();
        for (ProductDBO productDBO : productDBOSet) {
            products.add(mapProductDBOToProduct(productDBO));
        }
        return products;
    }

    /**
     * Method for mapping ResultSet to ProductDBO.
     * @param resultSet ResultSet.
     */
    public static ProductDBO mapResultSetToProductDBO(ResultSet resultSet) throws SQLException {
        return new ProductDBO.Builder(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .categoryId(resultSet.getLong("category_id"))
                .description(resultSet.getString("description"))
                .build();
    }

    /**
     * Method for mapping result set to productBDO
     */
    public static CompanyProductDBO mapResultSetToCompanyProductDBO(ResultSet resultSet) throws SQLException {
        return new CompanyProductDBO.Builder(resultSet.getLong("id"))
                .companyId(resultSet.getLong("company_id"))
                .productId(resultSet.getLong("product_id"))
                .price(new Price(resultSet.getBigDecimal("price")))
                .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                .build();
    }

    /**
     * Method for mapping CompanyProductDBO to CompanyProduct.
     * @param companyProductDBO CompanyProductDBO.
     * @param finding Finding.
     */
    public static CompanyProduct mapCompanyProductDBOToCompanyProduct(CompanyProductDBO companyProductDBO, String finding) throws DatabaseConnectionActiveException {
        if (finding.equals("company")) {
            return new CompanyProduct.Builder(companyProductDBO.getId())
                    .company(companyRepository.findById(companyProductDBO.getCompanyId())
                            .orElseThrow(() -> new RepositoryQueryException("Company not found")))
                    .product(new Product.Builder(companyProductDBO.getProductId()).build())
                    .price(companyProductDBO.getPrice())
                    .createdAt(companyProductDBO.getCreatedAt())
                    .build();
        }

        return new CompanyProduct.Builder(companyProductDBO.getId())
                .company(new Company.Builder(companyProductDBO.getCompanyId(), companyProductDBO.getName()).build())
                .product(productRepository.findByIdWithoutCompanies(companyProductDBO.getProductId())
                        .orElseThrow(() -> new RepositoryQueryException("Product not found")))
                .price(companyProductDBO.getPrice())
                .createdAt(companyProductDBO.getCreatedAt())
                .build();
    }

    /**
     * Method for mapping ResultSet to CompanyDBO.
     */
    public static UserCompanyDBO mapResultSetToUserCompanyDBO(ResultSet resultSet) throws SQLException {
        return new UserCompanyDBO.Builder(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .companyId(resultSet.getLong("company_id"))
                .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
