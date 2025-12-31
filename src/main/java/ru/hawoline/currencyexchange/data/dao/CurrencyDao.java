package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.domain.dao.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.data.CurrencyEntityMapper;
import ru.hawoline.currencyexchange.domain.dao.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<CurrencyEntity, String> {
    private Connection connection = new Connector().getConnection();
    private CurrencyEntityMapper currencyEntityMapper = new CurrencyEntityMapper();

    @Override
    public List<CurrencyEntity> getAll() {
        List<CurrencyEntity> currencies = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            String result = "SELECT * FROM Currencies";
            try (ResultSet resultSet = statement.executeQuery(result)){
                while (resultSet.next()) {
                    CurrencyEntity currency = currencyEntityMapper.fromResultSet(resultSet);
                    currencies.add(currency);
                }
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public CurrencyEntity getById(long id) {
        String sql = "SELECT * FROM Currencies WHERE ID = '"+ id +"';";
        try(ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            return currencyEntityMapper.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CurrencyEntity getBySpecification(String specification) {
        String sql = "SELECT * FROM Currencies WHERE Code = '"+ specification +"';";
        try(ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            return currencyEntityMapper.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


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
