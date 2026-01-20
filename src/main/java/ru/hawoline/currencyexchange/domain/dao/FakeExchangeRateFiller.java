package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

public class FakeExchangeRateFiller {
    private final FakeExchangeRateDao fakeExchangeRateDao;
    private final FakeCurrencyDao fakeCurrencyDao;
    private static final int NO_ID = -1;
    private final CurrencyDto firstCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAA",
            "AAA",
            "A"
    );
    private final CurrencyDto secondCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAB",
            "AAB",
            "B"
    );
    private final CurrencyDto thirdCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAC",
            "AAC",
            "C"
    );
    final CurrencyDto fourthCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAD",
            "AAD",
            "D"
    );
    public FakeExchangeRateFiller(FakeExchangeRateDao fakeExchangeRateDao, FakeCurrencyDao fakeCurrencyDao) {
        this.fakeExchangeRateDao = fakeExchangeRateDao;
        this.fakeCurrencyDao = fakeCurrencyDao;
    }

    public void fillCurrencies() throws ValueNotFoundException, DuplicateValueInDbException {
        fakeCurrencyDao.save(firstCurrencyDtoWithoutId);
        fakeCurrencyDao.save(secondCurrencyDtoWithoutId);
        fakeCurrencyDao.save(thirdCurrencyDtoWithoutId);
        fakeCurrencyDao.save(fourthCurrencyDtoWithoutId);
    }

    public void fillExchangeRates() throws DuplicateValueInDbException, ValueNotFoundException {
        final ExchangeRateDto firstExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                10
        );
        fakeExchangeRateDao.save(firstExchangeRateDtoBeforeSave);

        final ExchangeRateDto secondExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(thirdCurrencyDtoWithoutId.getCode()),
                20
        );
        fakeExchangeRateDao.save(secondExchangeRateDtoBeforeSave);

        final ExchangeRateDto thirdExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(fourthCurrencyDtoWithoutId.getCode()),
                30
        );
        fakeExchangeRateDao.save(thirdExchangeRateDtoBeforeSave);

        final ExchangeRateDto fourthExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(thirdCurrencyDtoWithoutId.getCode()),
                40
        );
        fakeExchangeRateDao.save(fourthExchangeRateDtoBeforeSave);
    }
}
