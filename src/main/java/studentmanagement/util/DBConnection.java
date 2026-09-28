package studentmanagement.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central place that knows how to open a JDBC connection.
 *
 * IMPORTANT (assignment requirement): the database URL/username/password
 * are NOT hard-coded here. They are read from "db.properties", a file
 * that is excluded from Git via .gitignore, so credentials never get
 * committed to GitHub.
 *
 * Every DAO class calls DBConnection.getConnection() inside a
 * try-with-resources block, so the Connection is always closed properly,
 * even if an exception is thrown.
 */
public class DBConnection {

    private static Properties props = null;

    private static void loadProperties() throws IOException {
        if (props == null) {
            props = new Properties();
            // db.properties must sit in the project root (same folder as pom.xml)
            try (FileInputStream fis = new FileInputStream("db.properties")) {
                props.load(fis);
            }
        }
    }

    /**
     * Opens and returns a brand-new database connection.
     * Caller is responsible for closing it (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        try {
            loadProperties();
        } catch (IOException e) {
            // Wrap the checked IOException into a SQLException so callers
            // only need to deal with one type of checked exception.
            throw new SQLException(
                    "Could not read db.properties. Make sure the file exists in the project root " +
                    "(copy db.properties.example to db.properties and fill in your details).", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}
