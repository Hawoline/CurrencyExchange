package ru.hawoline.currencyexchange.domain.dao;

import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FakeCurrencyDaoTest {
    private FakeCurrencyDao fakeCurrencyDao = new FakeCurrencyDao();
    private final int noId = -1;
    private final CurrencyDto firstCurrencyDtoWithoutId = new CurrencyDto(
            noId,
            "Dollar",
            "USD",
            "$"
    );
    private final CurrencyDto secondCurrencyDtoWithoutId = new CurrencyDto(
            noId,
            "Dollar",
            "AAA",
            "$"
    );

    @Test
    public void testSave() throws DuplicateValueInDbException {
        CurrencyDto currencyDtoWithId = null;
        try {
            currencyDtoWithId = fakeCurrencyDao.save(firstCurrencyDtoWithoutId);
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        int firstCurrencyId = 0;
        assertEquals(firstCurrencyId, currencyDtoWithId.getId());


        CurrencyDto secondCurrencyDto = null;
        try {
            secondCurrencyDto = fakeCurrencyDao.save(secondCurrencyDtoWithoutId);
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        int secondCurrencyId = 1;
        assertEquals(secondCurrencyId, secondCurrencyDto.getId());

        testSaveDuplicatedCurrency(secondCurrencyDto);
    }

    private void testSaveDuplicatedCurrency(CurrencyDto toDuplicate) {
        CurrencyDto duplicated = null;
        try {
            duplicated = fakeCurrencyDao.save(toDuplicate);
            int unreachableRandomId = 3;
            assertEquals(unreachableRandomId, duplicated.getId());
        } catch (DuplicateValueInDbException _) {
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetByLongId() throws DuplicateValueInDbException {
        testSave();
        try {
            assertEquals(0, fakeCurrencyDao.getByLongId(0).getId());
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            assertEquals(1, fakeCurrencyDao.getByLongId(1).getId());
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetBy() throws ValueNotFoundException, DuplicateValueInDbException {
        testSave();
        String firstCurrencyCode = firstCurrencyDtoWithoutId.getCode();
        assertEquals(0, fakeCurrencyDao.getBy(firstCurrencyCode).getId());
        assertEquals("USD", fakeCurrencyDao.getBy(firstCurrencyCode).getCode());
    }

    @Test
    public void testGetAll() throws DuplicateValueInDbException {
        testSave();
        int currenciesCount = 2;
        assertEquals(currenciesCount, fakeCurrencyDao.getAll().size());
    }

    // Вообще по тз обновление не предусмотрено, но пусть будет. WARN: я протестил не все кейсы из-за ненадобности
    @Test
    public void testUpdate() throws DuplicateValueInDbException, ValueNotFoundException {
        testSave();
        CurrencyDto forUpdate = new CurrencyDto(
                noId,
                "Dollar",
                "DAD",
                "$"
        );
        fakeCurrencyDao.update(forUpdate, "USD");
        CurrencyDto updatedCurrencyDto = fakeCurrencyDao.getBy("DAD");
        assertEquals("DAD", updatedCurrencyDto.getCode());
        assertEquals("$", updatedCurrencyDto.getSign());
    }
}