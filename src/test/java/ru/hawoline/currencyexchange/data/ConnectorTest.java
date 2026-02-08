package ru.hawoline.currencyexchange.data;

import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTest {

    @Test
    public void testConnector() {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();
        assertNotNull(connection);
        Dao<CurrencyEntity, String> dao = new CurrencyDao();
        List<CurrencyEntity> currencies = dao.getAll();

        assertFalse(currencies.isEmpty());
    }
}