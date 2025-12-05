package ru.hawoline.currencyexchange.data.repository.storage;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateInsertEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateResponse;
import ru.hawoline.currencyexchange.domain.repository.DataSource;

import java.sql.*;

public class ExchangeRateSqlDataSource implements DataSource<ExchangeRateInsertEntity, Long> {
    private Connection connection = new Connector().getConnection();

    @Override
    public Long save(ExchangeRateInsertEntity entity) {
        boolean isAdded = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            preparedStatement.setInt(1, entity.getBaseCurrencyId());
            preparedStatement.setInt(2, entity.getTargetCurrencyId());
            preparedStatement.setDouble(3, entity.getRate());

            int affectedRows = preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
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
    public ExchangeRateInsertEntity load(Long id) {
        return null;
    }
}
