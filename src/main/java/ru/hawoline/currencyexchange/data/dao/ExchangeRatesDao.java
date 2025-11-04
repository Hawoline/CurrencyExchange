package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.data.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.Dao;
import ru.hawoline.currencyexchange.domain.ExchangeRate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRatesDao implements Dao<ExchangeRateEntity> {
    private Connection connection = new Connector().getConnection();
    private Dao<CurrencyEntity> currenciesDao = new CurrenciesDao();

    @Override
    public void save(ExchangeRateEntity exchangeRate) {

    }

    @Override
    public ExchangeRateEntity get(int id) {
        return null;
    }

    @Override
    public List<ExchangeRateEntity> getAll() {
        List<ExchangeRateEntity> exchangeRates = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            String result = "SELECT * FROM ExchangeRates";
            ResultSet resultSet = statement.executeQuery(result);
            while (resultSet.next()) {
                ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity(
                        resultSet.getInt("ID"),
                        currenciesDao.get(resultSet.getInt("BaseCurrencyId")),
                        currenciesDao.get(resultSet.getInt("TargetCurrencyId")),
                        resultSet.getDouble("Rate")
                );
                exchangeRates.add(exchangeRateEntity);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    @Override
    public void delete(ExchangeRateEntity exchangeRate) {

    }
}
