package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Category;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.repository.AddressRepository;
import hr.tvz.productpricemonitoringtool.repository.CategoryRepository;
import hr.tvz.productpricemonitoringtool.repository.CompanyRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectMapper {

    private static final AddressRepository addressRepository = new AddressRepository();
    private static final CompanyRepository companyRepository = new CompanyRepository();
    private static final CategoryRepository categoryRepository = new CategoryRepository();

    private ObjectMapper() {}

    public static User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User.Builder(resultSet.getLong("id"), resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(Role.valueOf(resultSet.getString("role")))
                .companies(companyRepository.findAllByUserId(resultSet.getLong("id")))
                .build();
    }

    public static Company mapResultSetToCompany(ResultSet resultSet) throws SQLException {
        return new Company.Builder(resultSet.getLong("id"), resultSet.getString("name"))
                .address(addressRepository.findById(resultSet.getLong("address_id"))
                        .orElseThrow(() -> new SQLException("Address not found")))
                .build();
    }

    public static Address mapResultSetToAddress(ResultSet resultSet) throws SQLException {
        return new Address.Builder(resultSet.getLong("id"))
                .street(resultSet.getString("street"))
                .city(resultSet.getString("city"))
                .postalCode(resultSet.getString("postal_code"))
                .country(resultSet.getString("country"))
                .houseNumber(resultSet.getString("house_number"))
                .build();
    }

    public static Category mapResultSetToCategory(ResultSet resultSet) throws SQLException {
        return new Category.Builder(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .parentCategory(categoryRepository.findById(resultSet.getLong("parent_category_id")))
                .build();
    }
}
