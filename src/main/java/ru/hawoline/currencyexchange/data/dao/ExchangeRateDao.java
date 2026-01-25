package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.domain.dao.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.CurrencyIdPair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRateDao implements Dao<ExchangeRateEntity, CurrencyIdPair> {
    private Connection connection = new Connector().getConnection();

    @Override
    public ExchangeRateEntity create(ExchangeRateEntity exchangeRateEntity) throws DuplicateEntityException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            preparedStatement.setInt(1, exchangeRateEntity.baseCurrencyId());
            preparedStatement.setInt(2, exchangeRateEntity.targetCurrencyId());
            preparedStatement.setDouble(3, exchangeRateEntity.rate());

            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return new ExchangeRateEntity(
                            generatedId,
                            exchangeRateEntity.baseCurrencyId(),
                            exchangeRateEntity.targetCurrencyId(),
                            exchangeRateEntity.rate()
                    );
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DuplicateEntityException();
        }
    }

    @Override
    public ExchangeRateEntity getEntityById(CurrencyIdPair currencyIdPair) throws ExchangeRateNotFoundException {
        int baseCurrencyId = currencyIdPair.baseCurrencyId();
        int targetCurrencyId = currencyIdPair.targetCurrencyCode();
        String sql = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, baseCurrencyId);
            statement.setLong(2, targetCurrencyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new ExchangeRateNotFoundException("Exchange Rate with selected Currency Codes does not exist in Db");
                }
                return new ExchangeRateEntity(
                        resultSet.getInt("ID"),
                        baseCurrencyId,
                        targetCurrencyId,
                        resultSet.getDouble("Rate")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRateEntity> getAll() {
        List<ExchangeRateEntity> exchangeRates = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            String result = "SELECT * FROM ExchangeRates";
            try (ResultSet resultSet = statement.executeQuery(result)) {
                while (resultSet.next()) {
                    ExchangeRateEntity exchangeRateResponse = new ExchangeRateEntity(
                            resultSet.getInt("ID"),
                            resultSet.getInt("BaseCurrencyId"),
                            resultSet.getInt("TargetCurrencyId"),
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
    public void update(ExchangeRateEntity entity) {
        String sql = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, entity.rate());
            preparedStatement.setLong(2, entity.id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRateEntity getByIntId(int id) throws EntityNotFoundException {
        String sql = "SELECT * FROM ExchangeRates WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new EntityNotFoundException("Exchange Rate with selected ID does not exist in Db");
                }
                return new ExchangeRateEntity(
                        resultSet.getInt("ID"),
                        resultSet.getInt("BaseCurrencyId"),
                        resultSet.getInt("TargetCurrencyId"),
                        resultSet.getDouble("Rate")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
