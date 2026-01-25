package ru.hawoline.currencyexchange.data;

import ru.hawoline.currencyexchange.domain.dto.CurrencyEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencySqlMapper {
    public CurrencyEntity fromResultSet(ResultSet resultSet) throws SQLException {
        return new CurrencyEntity(
                resultSet.getInt("id"),
                resultSet.getString("FullName"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }
}
