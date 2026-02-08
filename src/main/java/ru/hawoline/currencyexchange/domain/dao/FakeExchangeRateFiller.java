package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

public class FakeExchangeRateFiller {
    private final FakeExchangeRateDao fakeExchangeRateDao;
    private final FakeCurrencyDao fakeCurrencyDao;
    private static final int NO_ID = -1;
    private final CurrencyEntity firstCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAA",
            "AAA",
            "A"
    );
    private final CurrencyEntity secondCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAB",
            "AAB",
            "B"
    );
    private final CurrencyEntity thirdCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAC",
            "AAC",
            "C"
    );
    final CurrencyEntity fourthCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAD",
            "AAD",
            "D"
    );
    public FakeExchangeRateFiller(FakeExchangeRateDao fakeExchangeRateDao, FakeCurrencyDao fakeCurrencyDao) {
        this.fakeExchangeRateDao = fakeExchangeRateDao;
        this.fakeCurrencyDao = fakeCurrencyDao;
    }

    public void fillCurrencies() throws DuplicateEntityException {
        fakeCurrencyDao.create(firstCurrencyEntityWithoutId);
        fakeCurrencyDao.create(secondCurrencyEntityWithoutId);
        fakeCurrencyDao.create(thirdCurrencyEntityWithoutId);
        fakeCurrencyDao.create(fourthCurrencyEntityWithoutId);
    }

    public void fillExchangeRates() throws DuplicateEntityException, EntityNotFoundException {
        final ExchangeRateEntity firstExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()),
                10
        );
        fakeExchangeRateDao.create(firstExchangeRateDtoBeforeSave);

        final ExchangeRateEntity secondExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityById(thirdCurrencyEntityWithoutId.getCode()),
                20
        );
        fakeExchangeRateDao.create(secondExchangeRateDtoBeforeSave);

        final ExchangeRateEntity thirdExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityById(fourthCurrencyEntityWithoutId.getCode()),
                30
        );
        fakeExchangeRateDao.create(thirdExchangeRateDtoBeforeSave);

        final ExchangeRateEntity fourthExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityById(thirdCurrencyEntityWithoutId.getCode()),
                40
        );
        fakeExchangeRateDao.create(fourthExchangeRateDtoBeforeSave);
    }
}
