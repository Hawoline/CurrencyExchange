package ru.hawoline.currencyexchange.data.dao;

import ru.hawoline.currencyexchange.data.Connector;
import ru.hawoline.currencyexchange.data.CurrencyMapper;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.Dao;
import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<CurrencyDto, String> {
    private Connection connection = new Connector().getConnection();
    private CurrencyMapper currencyMapper = new CurrencyMapper();

    @Override
    public List<CurrencyDto> getAll() {
        List<CurrencyDto> currencies = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            String result = "SELECT * FROM Currencies";
            try (ResultSet resultSet = statement.executeQuery(result)) {
                while (resultSet.next()) {
                    CurrencyDto currency = currencyMapper.fromResultSet(resultSet);
                    currencies.add(currency);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    @Override
    public CurrencyDto save(CurrencyDto currencyDtoWithoutId) {
        String sql = "insert into Currencies(Code, FullName, Sign) VALUES (" +
                "'" + currencyDtoWithoutId.getCode() + "', " +
                "'" + currencyDtoWithoutId.getName() + "', " +
                "'" + currencyDtoWithoutId.getSign() + "');";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            return currencyDtoWithoutId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public CurrencyDto getByLongId(long id) {
        String sql = "SELECT * FROM Currencies WHERE ID = '" + id + "';";
        try (ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            return currencyMapper.fromResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CurrencyDto getBy(String currencyCode) throws CurrencyNotFoundException {
        String sql = "SELECT * FROM Currencies WHERE Code = '" + currencyCode + "';";
        try (ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            if (!resultSet.next()) {
                throw new CurrencyNotFoundException(currencyCode);
            }
            return currencyMapper.fromResultSet(resultSet);
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
    public void delete(CurrencyDto currencyDto) {

    }

    @Override
    public void update(CurrencyDto currencyDto, String currencyCode) {

    }
}
