package hr.tvz.productpricemonitoringtool.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {

    public static Connection connectToDatabase() throws IOException, SQLException {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(Constants.DATABASE_PROPERTIES_FILE)) {
            props.load(reader);
        }

        return DriverManager.getConnection(
                props.getProperty("databaseUrl"),
                props.getProperty("username"),
                props.getProperty("password"));
    }

    public void disconnectFromDatabase(Connection connection) throws SQLException {
        connection.close();
    }
}