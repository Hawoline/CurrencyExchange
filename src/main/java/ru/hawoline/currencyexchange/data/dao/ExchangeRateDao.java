package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.ExchangeRateId;
import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;

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
                } else {
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
    public ExchangeRateDto getBy(ExchangeRateId exchangeRateId) throws CurrencyNotFoundException, ExchangeRateNotFoundException {
        CurrencyDto baseCurrencyDto = currencyDao.getBy(exchangeRateId.baseCurrencyCode());
        CurrencyDto targetCurrencyDto = currencyDao.getBy(exchangeRateId.targetCurrencyCode());
        int baseCurrencyId = baseCurrencyDto.getId();
        int targetCurrencyId = targetCurrencyDto.getId();
        String sql = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, baseCurrencyId);
            statement.setLong(2, targetCurrencyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new ExchangeRateNotFoundException("Exchange Rate with selected Currency Codes does not exist in Db");
                }
                return new ExchangeRateDto(
                        resultSet.getLong("ID"),
                        baseCurrencyDto,
                        targetCurrencyDto,
                        resultSet.getDouble("Rate")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(ExchangeRateId exchangeRateId) {
        if (!currencyDao.exists(exchangeRateId.baseCurrencyCode()) || !currencyDao.exists(exchangeRateId.targetCurrencyCode())) {
            return false;
        }
        int baseCurrencyId = 0;
        try {
            baseCurrencyId = currencyDao.getBy(exchangeRateId.baseCurrencyCode()).getId();
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        int targetCurrencyId = 0;
        try {
            targetCurrencyId = currencyDao.getBy(exchangeRateId.targetCurrencyCode()).getId();
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }

        String sql = "SELECT EXISTS(SELECT 1 FROM ExchangeRates WHERE BaseCurrencyId = ? and TargetCurrencyId = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
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
            e.printStackTrace();
        }
        return exchangeRates;
    }

    @Override
    public void update(ExchangeRateDto exchangeRateDto, ExchangeRateId exchangeRateId) {
        String sql = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, exchangeRateDto.getRate());
            preparedStatement.setLong(2, exchangeRateDto.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
