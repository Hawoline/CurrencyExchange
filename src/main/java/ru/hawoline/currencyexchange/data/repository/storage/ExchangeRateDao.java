package ru.hawoline.currencyexchange.data.repository.storage;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.data.repository.CurrencyDao;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateResponse;
import ru.hawoline.currencyexchange.domain.dao.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRateDao implements Dao<ExchangeRateResponse, Integer> {
    private Connection connection = new Connector().getConnection();
    private CurrencyDao currencyDao = new CurrencyDao();

    @Override
    public void save(ExchangeRateResponse exchangeRate) {

    }

    @Override
    public ExchangeRateResponse get(Integer id) {
        return null;
    }

    public ExchangeRateResponse get(String baseCurrencyCode, String targetCurrencyCode) {
        List<ExchangeRateResponse> exchangeRates = getAll();
        for (ExchangeRateResponse exchangeRate: exchangeRates) {
            boolean baseCurrencyCodeFromDbEqualsToUser = exchangeRate.getBaseCurrency().getCode().equals(baseCurrencyCode);
            boolean targetCurrencyCodeFromDbEqualsToUser = exchangeRate.getTargetCurrency().getCode().equals(targetCurrencyCode);
            if ( baseCurrencyCodeFromDbEqualsToUser && targetCurrencyCodeFromDbEqualsToUser) {
                return exchangeRate;
            }
        }

        throw new IllegalArgumentException("exchangeRateNotFound");
    }

    public boolean exists(String baseCurrencyCode, String targetCurrencyCode) {
        if (!currencyDao.exists(baseCurrencyCode) || !currencyDao.exists(targetCurrencyCode)) {
            return false;
        }
        int baseCurrencyId = currencyDao.get(baseCurrencyCode).getId();
        int targetCurrencyId = currencyDao.get(targetCurrencyCode).getId();

        try {
            String sql = "SELECT EXISTS(SELECT 1 FROM ExchangeRates WHERE BaseCurrencyId = ? and TargetCurrencyId = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                final int exchangeRateFound = 1;
                return rs.getInt(1) == exchangeRateFound;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<ExchangeRateResponse> getAll() {
        List<ExchangeRateResponse> exchangeRates = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            String result = "SELECT * FROM ExchangeRates";
            try (ResultSet resultSet = statement.executeQuery(result)) {
                while (resultSet.next()) {
                    ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse(
                            resultSet.getInt("ID"),
                            currencyDao.get(resultSet.getInt("BaseCurrencyId")),
                            currencyDao.get(resultSet.getInt("TargetCurrencyId")),
                            resultSet.getDouble("Rate")
                    );
                    exchangeRates.add(exchangeRateResponse);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    @Override
    public void delete(ExchangeRateResponse exchangeRate) {

    }


}
