package ru.hawoline.currencyexchange.data;

import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.Currency;
import ru.hawoline.currencyexchange.domain.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDao implements Dao<CurrencyEntity, String> {
    private Connection connection = new ConnectionManager().getConnection();

    @Override
    public List<CurrencyEntity> getAll() {
        List<CurrencyEntity> currencies = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
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
        String sql = "insert into Currencies(Code, FullName, Sign) VALUES (" +
                "'"+ currencyEntity.getCode() + "', " +
                "'"+ currencyEntity.getName() + "', " +
                "'"+ currencyEntity.getSign() + "');";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CurrencyEntity get(String code) {
        String sql = "SELECT * FROM Currencies WHERE Code = '"+ code +"';";
        try(ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            return new CurrencyEntity(
                    resultSet.getInt("id"),
                    resultSet.getString("FullName"),
                    resultSet.getString("code"),
                    resultSet.getString("sign")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String code) {
        try {
            String sql = "SELECT EXISTS(SELECT 1 FROM Currencies WHERE code = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, code);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void delete(CurrencyEntity currencyEntity) {

    }
}
