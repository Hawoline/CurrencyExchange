package ru.hawoline.currencyexchange.domain.dao;

import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.dto.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FakeCurrencyDaoTest {
    private FakeCurrencyDao fakeCurrencyDao = new FakeCurrencyDao();
    private final int noId = -1;
    private final CurrencyEntity firstCurrencyEntityWithoutId = new CurrencyEntity(
            noId,
            "Dollar",
            "USD",
            "$"
    );
    private final CurrencyEntity secondCurrencyEntityWithoutId = new CurrencyEntity(
            noId,
            "Dollar",
            "AAA",
            "$"
    );

    @Test
    public void testCreate() throws DuplicateEntityException {
        CurrencyEntity currencyEntityWithId = null;
        currencyEntityWithId = fakeCurrencyDao.create(firstCurrencyEntityWithoutId);
        int firstCurrencyId = 0;
        assertEquals(firstCurrencyId, currencyEntityWithId.getId());


        CurrencyEntity secondCurrencyEntity = null;
        secondCurrencyEntity = fakeCurrencyDao.create(secondCurrencyEntityWithoutId);
        int secondCurrencyId = 1;
        assertEquals(secondCurrencyId, secondCurrencyEntity.getId());

        testCreateDuplicatedCurrency(secondCurrencyEntity);
    }

    private void testCreateDuplicatedCurrency(CurrencyEntity toDuplicate) {
        CurrencyEntity duplicated = null;
        try {
            duplicated = fakeCurrencyDao.create(toDuplicate);
            int unreachableRandomId = 3;
            assertEquals(unreachableRandomId, duplicated.getId());
        } catch (DuplicateEntityException _) {
        }
    }

    @Test
    public void testGetEntityByIdLongId() throws DuplicateEntityException {
        testCreate();
        try {
            assertEquals(0, fakeCurrencyDao.getByIntId(0).getId());
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            assertEquals(1, fakeCurrencyDao.getByIntId(1).getId());
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetEntityById() throws EntityNotFoundException, DuplicateEntityException {
        testCreate();
        String firstCurrencyCode = firstCurrencyEntityWithoutId.getCode();
        assertEquals(0, fakeCurrencyDao.getEntityById(firstCurrencyCode).getId());
        assertEquals("USD", fakeCurrencyDao.getEntityById(firstCurrencyCode).getCode());
    }

    @Test
    public void testGetAll() throws DuplicateEntityException {
        testCreate();
        int currenciesCount = 2;
        assertEquals(currenciesCount, fakeCurrencyDao.getAll().size());
    }

}