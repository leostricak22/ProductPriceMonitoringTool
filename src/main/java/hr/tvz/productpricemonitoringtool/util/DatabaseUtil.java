package hr.tvz.productpricemonitoringtool.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseUtil class.
 * Contains methods for connecting and disconnecting from database.
 */
public class DatabaseUtil {

    private static Boolean activeConnectionWithDatabase = false;

    public static Connection connectToDatabase() throws IOException, SQLException {
        Properties props = PropsUtil.getProperties(Constants.DATABASE_PROPERTIES_FILE);
        return DriverManager.getConnection(
                props.getProperty("databaseUrl"),
                props.getProperty("username"),
                props.getProperty("password"));
    }

    public void disconnectFromDatabase(Connection connection) throws SQLException {
        connection.close();
    }

    public static Boolean isActiveConnectionWithDatabase() {
        return activeConnectionWithDatabase;
    }

    public static void setActiveConnectionWithDatabase(Boolean activeConnectionWithDatabase) {
        DatabaseUtil.activeConnectionWithDatabase = activeConnectionWithDatabase;
    }
}