package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static final String URL =
            "jdbc:postgresql://ep-fragrant-wind-ax2ayd7p.c-4.us-east-2.aws.neon.tech:5432/neondb?sslmode=require" +
                    "&loginTimeout=60&socketTimeout=120";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_rLgYtB79qCQf";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
