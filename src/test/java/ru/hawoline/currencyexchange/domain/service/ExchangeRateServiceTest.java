package ru.hawoline.currencyexchange.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.dao.FakeCurrencyDao;
import ru.hawoline.currencyexchange.domain.dao.FakeExchangeRateDao;
import ru.hawoline.currencyexchange.domain.dao.FakeExchangeRateFiller;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

class ExchangeRateServiceTest {
    private final FakeCurrencyDao fakeCurrencyDao = new FakeCurrencyDao();
    private final FakeExchangeRateDao fakeExchangeRateDao = new FakeExchangeRateDao();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(fakeExchangeRateDao, fakeCurrencyDao);
    private FakeExchangeRateFiller fakeExchangeRateFiller = new FakeExchangeRateFiller(fakeExchangeRateDao, fakeCurrencyDao);

    @BeforeEach
    public void setUp() throws EntityNotFoundException, DuplicateEntityException {
        fakeExchangeRateFiller.fillCurrencies();
        fakeExchangeRateFiller.fillExchangeRates();
    }

    @Test
    public void testAddExchangeRate() {

    }

}