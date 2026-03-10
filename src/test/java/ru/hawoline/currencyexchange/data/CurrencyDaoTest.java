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
    @BeforeEach
    protected void setUp() throws DuplicateEntityException {
        fakeCurrencyDao.delete(firstCurrencyEntityWithoutId.getCode());
        fakeCurrencyDao.delete(secondCurrencyEntityWithoutId.getCode());
        create();
    }

    public void create() throws DuplicateEntityException {
        CurrencyEntity currencyEntityWithId = fakeCurrencyDao.create(firstCurrencyEntityWithoutId);
        int firstCurrencyId = currencyEntityWithId.getId();
        assertTrue(0 < firstCurrencyId);


        CurrencyEntity secondCurrencyEntity = fakeCurrencyDao.create(secondCurrencyEntityWithoutId);
        int secondCurrencyEntityId = secondCurrencyEntity.getId();
        assertTrue(firstCurrencyId < secondCurrencyEntityId);

        testCreateDuplicatedCurrency(secondCurrencyEntity);
    }

    private void testCreateDuplicatedCurrency(CurrencyEntity toDuplicate) {
        assertThrows(DuplicateEntityException.class, () -> fakeCurrencyDao.create(toDuplicate));
    }

    @Test
    public void testGetEntityByIdLong() {
        try {
            assertEquals(1, fakeCurrencyDao.getByIntId(1).getId());
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            assertEquals(2, fakeCurrencyDao.getByIntId(2).getId());
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
        int currenciesCount = 10;
        assertEquals(currenciesCount, fakeCurrencyDao.getAll().size());
    }

}