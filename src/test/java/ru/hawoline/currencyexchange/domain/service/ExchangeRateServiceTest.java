package ru.hawoline.currencyexchange.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.ExchangeRateService;
import ru.hawoline.currencyexchange.domain.dao.FakeCurrencyDao;
import ru.hawoline.currencyexchange.domain.dao.FakeExchangeRateDao;
import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExchangeRateServiceTest {
    private final FakeCurrencyDao fakeCurrencyDao = new FakeCurrencyDao();
    private final FakeExchangeRateDao fakeExchangeRateDao = new FakeExchangeRateDao();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(fakeExchangeRateDao, fakeCurrencyDao);

    //TODO finish filling exchangeRateService
    @BeforeEach
    public void setUp() throws EntityNotFoundException, DuplicateEntityException {
        fakeExchangeRateDao.removeAll();
        fillDirectRates();
//        fillReverseRates();
        fillCrossRates();
    }

    private void fillCrossRates() {
//        fillDirectCrossRates();
        fillReverseCrossRates();
    }

    private void fillReverseCrossRates() {

    }

    private void fillDirectRates() throws DuplicateEntityException, EntityNotFoundException {
        exchangeRateService.add(new AddExchangeRateDto("EUR", "ALL", 2));
    }

    @Test
    public void testConvertDirectRate() throws EntityNotFoundException {
        ConvertedExchangeRateDto converted = exchangeRateService.convert(new ExchangeDto("EUR", "ALL", 5));
        assertEquals(10, converted.convertedAmount());
    }

    @Test
    public void testConvertZeroAmount() throws EntityNotFoundException {
        ConvertedExchangeRateDto zero = exchangeRateService.convert(new ExchangeDto("EUR", "ALL", 0));
        assertEquals(0, zero.convertedAmount());
    }

    @Test
    public void testConvertReverseRate() throws EntityNotFoundException {
        ConvertedExchangeRateDto converted = exchangeRateService.convert(new ExchangeDto("ALL", "EUR", 5));
        assertEquals(0.5, converted.convertedAmount());
    }

    @Test
    public void testConvertCrossRate() throws EntityNotFoundException {
        convertAtDirectCrossRate();
        convertAtReverseCrossRate();
        convertAtSameQuotationCrossRate();
    }

    private void convertAtDirectCrossRate() throws EntityNotFoundException {
        ConvertedExchangeRateDto converted = exchangeRateService.convert(new ExchangeDto("EUR", "BRL", 5));
        assertEquals(600, converted.convertedAmount());
    }

    private void convertAtReverseCrossRate() throws EntityNotFoundException {
        ConvertedExchangeRateDto convertedBrlToEur = exchangeRateService.convert(new ExchangeDto("BRL", "EUR", 5));
        assertEquals(0.04, convertedBrlToEur.convertedAmount());
    }

    private void convertAtSameQuotationCrossRate() throws EntityNotFoundException{
        ConvertedExchangeRateDto convertedBrlToAll = exchangeRateService.convert(new ExchangeDto("BRL", "ALL", 5));
        assertEquals(0.42, convertedBrlToAll.convertedAmount());

        ConvertedExchangeRateDto convertedIfDollarInTargetsInDao = exchangeRateService.convert(new ExchangeDto("XAF", "CLF", 5));
        assertEquals(3, convertedIfDollarInTargetsInDao.convertedAmount());

        ConvertedExchangeRateDto convertedIfDollarInBasesInDao = exchangeRateService.convert(new ExchangeDto("CLP", "CNY", 5));
        assertEquals(3, convertedIfDollarInBasesInDao.convertedAmount());
    }
}