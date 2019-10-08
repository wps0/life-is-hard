package pl.wieczorekp.lifeishard.network.database;

import pl.wieczorekp.configmodule.IConfigurableJavaPlugin;
import pl.wieczorekp.lifeishard.ExceptionManager;
import pl.wieczorekp.lifeishard.LifeIsHard;

import java.sql.*;
import java.util.HashMap;

public abstract class DatabaseConnection {
    private HashMap<String, Object> databaseCredentials; // ToDo: usunac moze albo zmienic tak, zeby dla kazdego databaseconnection walic jedna instancje
    private Connection connection;
    protected final boolean isSQLite;

    /**
     * Sets connection mode to external database
     * version: 1.0.1
     *
     * @param databaseCredentials credentials HashMap
     */
    protected DatabaseConnection(HashMap<String, Object> databaseCredentials, boolean isSQLite) {
        this.databaseCredentials = databaseCredentials;
        this.connection = null;
        this.isSQLite = isSQLite;
        try {
            checkDbDrivers();
        } catch (ClassNotFoundException e) {
            ExceptionManager.handle(new IllegalArgumentException("current db driver not found! use alternative db"));
        }
    }

    protected synchronized void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        "jdbc:" + (isSQLite ? "sqlite:" + LifeIsHard.getInst().getDataFolder().getAbsolutePath() + "/" + databaseCredentials.get("name") + ".db"
                                : "mysql://" + databaseCredentials.get("address") + ":" + databaseCredentials.get("port") + "/" + databaseCredentials.get("name") + databaseCredentials.get("login") + databaseCredentials.get("password")
                        )
                );
            }
        } catch (SQLException e) {
            ExceptionManager.handle(e);
        }
    }
    protected synchronized void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                ExceptionManager.handle(e);
            }
        }
    }

    protected Connection getConnection() {
        return connection;
    }

    private void checkDbDrivers() throws ClassNotFoundException {
        if (isSQLite)
            Class.forName("org.sqlite.JDBC");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!connection.isClosed())
            connection.close();
    }
}
