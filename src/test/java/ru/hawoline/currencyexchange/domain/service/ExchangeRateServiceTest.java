package ru.hawoline.currencyexchange.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.dao.FakeCurrencyDao;
import ru.hawoline.currencyexchange.domain.dao.FakeExchangeRateDao;
import ru.hawoline.currencyexchange.domain.dao.FakeExchangeRateFiller;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

class ExchangeRateServiceTest {
    private final FakeCurrencyDao fakeCurrencyDao = new FakeCurrencyDao();
    private final FakeExchangeRateDao fakeExchangeRateDao = new FakeExchangeRateDao(fakeCurrencyDao);
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(fakeExchangeRateDao);
    private FakeExchangeRateFiller fakeExchangeRateFiller = new FakeExchangeRateFiller(fakeExchangeRateDao, fakeCurrencyDao);

    @BeforeEach
    public void setUp() throws ValueNotFoundException, DuplicateValueInDbException {
        fakeExchangeRateFiller.fillCurrencies();
        fakeExchangeRateFiller.fillExchangeRates();
    }

    @Test
    public void testAddExchangeRate() {

    }

}