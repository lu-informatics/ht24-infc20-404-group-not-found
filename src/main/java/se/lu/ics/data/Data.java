/**
 * This class provides a method to establish a connection to a SQL Server database.
 * 
 * The `getConnection` method reads database connection properties from a configuration file,
 * constructs a connection URL, and attempts to establish a connection using the JDBC DriverManager.
 * 
 * Key steps in the `getConnection` method:
 * 1. Load database connection properties from a file.
 * 2. Construct the database connection URL.
 * 3. Attempt to establish a connection to the database.
 * 4. Return the established connection or null if the connection fails.
 * 
 * Note: The method prints a success message if the connection is established and an error message if it fails.
 */

//-------------------------------------------------------IMPORTS

package se.lu.ics.data;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

//-------------------------------------------------------IMPORTS

// -------------------------------------------------------------------------------------------------CLASS

public class Data {

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Properties connectionProperties = new Properties();
            FileInputStream stream = new FileInputStream(
                    "/Users/michaelringberg/Desktop/JavaDB/maven-jdbc/src/main/resources/config.properties");
            connectionProperties.load(stream);

            String databaseServerName = connectionProperties.getProperty("database.server.name");
            String databaseServerPort = connectionProperties.getProperty("database.server.port");
            String databaseName = connectionProperties.getProperty("database.name");
            String databaseUsername = connectionProperties.getProperty("database.user.name");
            String databasePassword = connectionProperties.getProperty("database.user.password");

            String connectionURL = "jdbc:sqlserver://" + databaseServerName + ":" + databaseServerPort + ";"
                    + "databaseName=" + databaseName + ";"
                    + "user=" + databaseUsername + ";"
                    + "password=" + databasePassword + ";"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;";

            connection = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to the database successfully!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }
}

// -------------------------------------------------------------------------------------------------CLASS
