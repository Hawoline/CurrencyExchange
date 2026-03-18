package ru.hawoline.currencyexchange.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyDaoTest {
    private final Dao<CurrencyEntity, String> fakeCurrencyDao = new CurrencyDao();
    private final int noId = -1;
    private final CurrencyEntity firstCurrencyEntityWithoutId = new CurrencyEntity(
            noId,
            "AAA Currency",
            "AAA",
            "A"
    );
    private final CurrencyEntity secondCurrencyEntityWithoutId = new CurrencyEntity(
            noId,
            "AAB Currency",
            "AAB",
            "B"
    );

    @Test
    public void testGetEntityBy() throws EntityNotFoundException {
        String firstCurrencyWithoutIdCode = firstCurrencyEntityWithoutId.getCode();
        CurrencyEntity firstCurrencyEntity = fakeCurrencyDao.getEntityBy(firstCurrencyWithoutIdCode);
        int firstCurrencyId = firstCurrencyEntity.getId();
        assertTrue(0 < firstCurrencyId);

        assertEquals(firstCurrencyWithoutIdCode, firstCurrencyEntity.getCode());
    }

    @Test
    public void testGetAll() {
        int currenciesCount = 17;
        assertEquals(currenciesCount, fakeCurrencyDao.getAll().size());
    }

}