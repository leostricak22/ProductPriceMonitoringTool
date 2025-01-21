package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class ObjectMapper {

    private ObjectMapper() {}

    public static User mapResultSetToCategory(ResultSet resultSet) throws SQLException {
        return new User.Builder(resultSet.getLong("id"), resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(Role.valueOf(resultSet.getString("role")))
                .companies(new HashSet<>())
                .build();
    }
}
