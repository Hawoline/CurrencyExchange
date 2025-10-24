package ru.hawoline.currencyexchange.data;

import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.Currency;
import ru.hawoline.currencyexchange.domain.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDao implements Dao<CurrencyEntity> {


    @Override
    public List<CurrencyEntity> getAll() {
        List<CurrencyEntity> currencies = new ArrayList<>();

        ConnectionManager connectionManager = new ConnectionManager();
        try (Statement statement = connectionManager.getConnection().createStatement()) {
            String result = "SELECT * FROM Currencies";
            ResultSet resultSet = statement.executeQuery(result);
            while (resultSet.next()) {
                CurrencyEntity currency = new CurrencyEntity(
                        resultSet.getInt("id"),
                        resultSet.getString("FullName"),
                        resultSet.getString("Code"),
                        resultSet.getString("Sign")
                );
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    @Override
    public void save(CurrencyEntity currencyEntity) {

    }

    @Override
    public void delete(CurrencyEntity currencyEntity) {

    }
}
