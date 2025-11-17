package com.solvd.schoolschedule.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Thread-safe database connection pool implementation using the Singleton pattern.
 * Manages a pool of reusable database connections to improve performance and resource utilization.
 *
 * <p>The pool is initialized with a fixed number of connections based on the configuration
 * in database.properties. Connections are borrowed from the pool and must be returned after use.</p>
 *
 * <p>Configuration is loaded from src/main/resources/db/database.properties which must contain:
 * <ul>
 *   <li>db.driver - JDBC driver class name</li>
 *   <li>db.url - Database connection URL</li>
 *   <li>db.username - Database username</li>
 *   <li>db.password - Database password</li>
 *   <li>db.pool.size - Number of connections in the pool (default: 10)</li>
 * </ul></p>
 *
 * @see ConnectionPool#getInstance()
 * @see ConnectionPool#getConnection()
 * @see ConnectionPool#releaseConnection(Connection)
 */
public class ConnectionPool {
    private static ConnectionPool instance;
    private final BlockingQueue<Connection> pool;
    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    /**
     * Private constructor to enforce Singleton pattern.
     * Loads database configuration from resources/db/database.properties and initializes
     * the connection pool with the specified number of connections.
     *
     * @throws RuntimeException if database.properties cannot be found or loaded,
     *                          if the JDBC driver cannot be loaded,
     *                          or if connections cannot be created
     */
    private ConnectionPool() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db/database.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find database.properties");
            }
            props.load(input);

            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            this.poolSize = Integer.parseInt(props.getProperty("db.pool.size", "10"));

            String driver = props.getProperty("db.driver");
            Class.forName(driver);

            this.pool = new ArrayBlockingQueue<>(poolSize);
            for (int i = 0; i < poolSize; i++) {
                pool.add(createConnection());
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    /**
     * Returns the singleton instance of the ConnectionPool.
     * Creates the instance on first call (lazy initialization).
     *
     * @return the singleton ConnectionPool instance
     */
    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    /**
     * Creates a new database connection using the configured credentials.
     *
     * @return a new Connection object
     * @throws SQLException if a database access error occurs
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Retrieves a connection from the pool.
     * Blocks if no connections are available until one is returned to the pool.
     *
     * <p><strong>Important:</strong> The borrowed connection must be returned to the pool
     * using {@link #releaseConnection(Connection)} after use to avoid connection exhaustion.</p>
     *
     * @return a Connection from the pool
     * @throws RuntimeException if the thread is interrupted while waiting for a connection
     */
    public Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get connection from pool", e);
        }
    }

    /**
     * Returns a connection to the pool for reuse.
     * Should be called in a finally block to ensure connections are always returned.
     *
     * @param connection the Connection to return to the pool (can be null)
     * @throws RuntimeException if the thread is interrupted while returning the connection
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                pool.put(connection);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to release connection to pool", e);
            }
        }
    }

    /**
     * Closes all connections in the pool and clears the pool.
     * Should be called when shutting down the application.
     *
     * <p>Note: This method swallows SQLExceptions to ensure all connections
     * are attempted to be closed even if some fail.</p>
     */
    public void closeAllConnections() {
        for (Connection connection : pool) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Print error but continue closing other connections to ensure cleanup
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        pool.clear();
    }
}
