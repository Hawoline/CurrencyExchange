package ru.hawoline.currencyexchange.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {/// dfvlkdfbn
    private static final String URL = "jdbc:sqlite:C:" + File.separator +
            "Users" + File.separator +
            "Hawoline" + File.separator +
            "IdeaProjects" + File.separator +
            "CurrencyExchange" + File.separator +
            "currency_exchange.sqlite";
    private static Connection connection;

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
