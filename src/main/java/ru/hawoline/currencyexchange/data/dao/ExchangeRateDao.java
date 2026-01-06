package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.domain.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.ValueNotFoundException;
import ru.hawoline.currencyexchange.domain.dao.ExchangeRateId;
import ru.hawoline.currencyexchange.domain.dao.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dao.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRateDao implements Dao<ExchangeRateDto, ExchangeRateId> {
    private Connection connection = new Connector().getConnection();
    private CurrencyDao currencyDao = new CurrencyDao();

    @Override
    public ExchangeRateDto save(ExchangeRateDto exchangeRateDto) throws DuplicateValueInDbException {
        if (exists(new ExchangeRateId(exchangeRateDto.getBaseCurrency().getCode(),
                exchangeRateDto.getTargetCurrency().getCode()))) {
            throw new DuplicateValueInDbException();
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            preparedStatement.setInt(1, exchangeRateDto.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRateDto.getTargetCurrency().getId());
            preparedStatement.setDouble(3, exchangeRateDto.getRate());

            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new ExchangeRateDto(generatedKeys.getLong(1), exchangeRateDto.getBaseCurrency(), exchangeRateDto.getTargetCurrency(), exchangeRateDto.getRate());
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ExchangeRateDto getByLongId(long id) {
        return null;
    }

    @Override
    public ExchangeRateDto getBy(ExchangeRateId id) {
        List<ExchangeRateDto> exchangeRates = getAll();
        for (ExchangeRateDto exchangeRate: exchangeRates) {
            boolean baseCurrencyCodeFromDbEqualsToUser = exchangeRate.getBaseCurrency().getCode().equals(id.getBaseCurrencyCode());
            boolean targetCurrencyCodeFromDbEqualsToUser = exchangeRate.getTargetCurrency().getCode().equals(id.getTargetCurrencyCode());
            if ( baseCurrencyCodeFromDbEqualsToUser && targetCurrencyCodeFromDbEqualsToUser) {
                return exchangeRate;
            }
        }

        throw new IllegalArgumentException("exchangeRateNotFound");
    }

    public boolean exists(ExchangeRateId exchangeRateId) {
        if (!currencyDao.exists(exchangeRateId.getBaseCurrencyCode()) || !currencyDao.exists(exchangeRateId.getTargetCurrencyCode())) {
            return false;
        }
        int baseCurrencyId = 0;
        try {
            baseCurrencyId = currencyDao.getBy(exchangeRateId.getBaseCurrencyCode()).getId();
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        int targetCurrencyId = 0;
        try {
            targetCurrencyId = currencyDao.getBy(exchangeRateId.getTargetCurrencyCode()).getId();
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }

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
    public List<ExchangeRateDto> getAll() {
        List<ExchangeRateDto> exchangeRates = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            String result = "SELECT * FROM ExchangeRates";
            try (ResultSet resultSet = statement.executeQuery(result)) {
                while (resultSet.next()) {
                    ExchangeRateDto exchangeRateResponse = new ExchangeRateDto(
                            resultSet.getInt("ID"),
                            currencyDao.getByLongId(resultSet.getInt("BaseCurrencyId")),
                            currencyDao.getByLongId(resultSet.getInt("TargetCurrencyId")),
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
    public void delete(ExchangeRateDto exchangeRate) {

    }


}
