package ru.hawoline.currencyexchange.domain.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FakeExchangeRateFillerTest {
    private final FakeCurrencyDao fakeCurrencyDao = new FakeCurrencyDao();
    private final FakeExchangeRateDao fakeExchangeRateDao = new FakeExchangeRateDao(fakeCurrencyDao);
    private FakeExchangeRateFiller fakeExchangeRateFiller = new FakeExchangeRateFiller(fakeExchangeRateDao, fakeCurrencyDao);
    @BeforeEach
    void setUp() throws ValueNotFoundException, DuplicateValueInDbException {
        fakeExchangeRateFiller.fillCurrencies();
        fakeExchangeRateFiller.fillExchangeRates();
    }

    @Test
    void testFakeExchangeRateFiller() {
        assertEquals(4, fakeCurrencyDao.getAll().size());
        assertEquals(4, fakeExchangeRateDao.getAll().size());
    }
}