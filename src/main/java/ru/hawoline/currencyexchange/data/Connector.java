package ru.hawoline.currencyexchange.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private static final String URL = "jdbc:sqlite:" + File.separator +
            "opt" + File.separator +
            "currency_exchange" + File.separator +
            "data" + File.separator +
            "currency_exchange.sqlite";
    private static final Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Db not found: " + e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
