package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String ipAddress = "//wgudb.ucertify.com:3306/";
    private static final String jdbcUrl = protocol + vendor + location + ipAddress + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection conn = null;  // Connection Interface

    public static Connection openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            conn = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
           // System.out.println("Error:" + e.getMessage());
            e.printStackTrace();

        }
        return conn;
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
           // System.out.println("Error:" + e.getMessage());
        }
    }
}