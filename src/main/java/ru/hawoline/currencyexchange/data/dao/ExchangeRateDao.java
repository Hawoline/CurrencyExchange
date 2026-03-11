package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyPairEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRateDao implements Dao<ExchangeRateEntity, CurrencyPairEntity> {
    private final Connection connection = new Connector().getConnection();

    @Override
    public ExchangeRateEntity create(ExchangeRateEntity exchangeRateEntity) throws DuplicateEntityException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            int baseCurrencyId = exchangeRateEntity.baseCurrency().getId();
            int targetCurrencyId = exchangeRateEntity.targetCurrency().getId();

            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setDouble(3, exchangeRateEntity.rate());

            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return new ExchangeRateEntity(
                            generatedId,
                            exchangeRateEntity.baseCurrency(),
                            exchangeRateEntity.targetCurrency(),
                            exchangeRateEntity.rate()
                    );
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DuplicateEntityException("Exchange Rate with " + exchangeRateEntity + " already exists.");
        }
    }

    @Override
    public ExchangeRateEntity getEntityBy(CurrencyPairEntity currencyPairEntity) throws ExchangeRateNotFoundException {
        CurrencyEntity currencyEntity = currencyPairEntity.base();
        CurrencyEntity targetCurrency = currencyPairEntity.target();
        String sql = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, currencyEntity.getId());
            statement.setLong(2, targetCurrency.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new ExchangeRateNotFoundException("Exchange Rate with selected Currency Codes does not exist in Db");
                }


                return new ExchangeRateEntity(
                        resultSet.getInt("ID"),
                        currencyEntity,
                        targetCurrency,
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
                    int baseCurrencyId = resultSet.getInt("BaseCurrencyId");
                    int targetCurrencyId = resultSet.getInt("TargetCurrencyId");

                    CurrencyEntity baseCurrencyEntity = new CurrencyEntity(
                            baseCurrencyId,
                            "",
                            "",
                            ""
                    );
                    CurrencyEntity targetCurrencyEntity = new CurrencyEntity(
                            targetCurrencyId,
                            "",
                            "",
                            ""
                    );

                    ExchangeRateEntity exchangeRateResponse = new ExchangeRateEntity(
                            resultSet.getInt("ID"),
                            baseCurrencyEntity,
                            targetCurrencyEntity,
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
            throw new RuntimeException(e); // TODO нормально обработать
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

                int baseCurrencyId = resultSet.getInt("BaseCurrencyId");
                CurrencyEntity baseCurrencyEntity = new CurrencyEntity(baseCurrencyId);
                CurrencyEntity targetCurrencyEntity = new CurrencyEntity(id);
                return new ExchangeRateEntity(
                        resultSet.getInt("ID"),
                        baseCurrencyEntity,
                        targetCurrencyEntity,
                        resultSet.getDouble("Rate")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(CurrencyPairEntity id) {
        // TODO realize
    }

}
