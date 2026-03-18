package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.data.CurrencySqlMapper;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<CurrencyEntity, String> {
    private final Connection connection = new Connector().getConnection();
    private final CurrencySqlMapper currencySqlMapper = new CurrencySqlMapper();

    @Override
    public List<CurrencyEntity> getAll() {
        List<CurrencyEntity> currencies = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            String result = "SELECT * FROM Currencies";
            try (ResultSet resultSet = statement.executeQuery(result)) {
                while (resultSet.next()) {
                    CurrencyEntity currency = currencySqlMapper.fromResultSet(resultSet);
                    currencies.add(currency);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    @Override
    public CurrencyEntity create(CurrencyEntity currencyEntityWithoutId) throws DuplicateEntityException {
        String sql = "insert into Currencies(Code, FullName, Sign) VALUES (?, ?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currencyEntityWithoutId.getCode());
            preparedStatement.setString(2, currencyEntityWithoutId.getName());
            preparedStatement.setString(3, currencyEntityWithoutId.getSign());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return new CurrencyEntity(
                            generatedId,
                            currencyEntityWithoutId.getCode(),
                            currencyEntityWithoutId.getName(),
                            currencyEntityWithoutId.getSign()
                    );
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DuplicateEntityException("Currency with code " + currencyEntityWithoutId.getCode() + " already exists.");
        }
    }

    @Override
    public CurrencyEntity getEntityBy(String currencyCode) throws CurrencyNotFoundException {
        String sql = "SELECT * FROM Currencies WHERE Code = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, currencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new CurrencyNotFoundException(currencyCode);
            }
            return currencySqlMapper.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new CurrencyNotFoundException(currencyCode);
        }
    }

        @Override
    public void update(CurrencyEntity entity) throws EntityNotFoundException {

    }
}
