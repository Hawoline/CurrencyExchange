package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

public class FakeExchangeRateFiller {
    private final FakeExchangeRateDao fakeExchangeRateDao;
    private final FakeCurrencyDao fakeCurrencyDao;
    private static final int NO_ID = -1;
    private final CurrencyEntity euroWithoutId = new CurrencyEntity(
            NO_ID,
            "Euro",
            "EUR",
            "A"
    );
    private final CurrencyEntity albanianLekWithoutId = new CurrencyEntity(
            NO_ID,
            "Albanian Lek",
            "ALL",
            "B"
    );
    private final CurrencyEntity dollarWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar",
            "USD",
            "C"
    );
    private final CurrencyEntity bruneiDollarWithoutId = new CurrencyEntity(
            NO_ID,
            "Brunei Dollar",
            "BND",
            "D"
    );
    private final CurrencyEntity brazilianRealWithoutId = new CurrencyEntity(
            NO_ID,
            "Brunei Dollar",
            "BRL",
            "D"
    );
    private final CurrencyEntity chadWithoutId = new CurrencyEntity(
            NO_ID,
            "CFA Franc BEAC",
            "XAF",
            "xaf Sign"
    );
    private final CurrencyEntity chileWithoutId = new CurrencyEntity(
            NO_ID,
            "Unidad de Fomento",
            "CLF",
            "chile sign"
    );
    private final CurrencyEntity chileanPesoWithoutId = new CurrencyEntity(
            NO_ID,
            "Chilean Peso",
            "CLP",
            "dkvmdv"
    );
    private final CurrencyEntity yuanRenminbiWithoutId = new CurrencyEntity(
            NO_ID,
            "Yuan Renminbi",
            "CNY",
            "dkvmdv"
    );

    public FakeExchangeRateFiller(FakeExchangeRateDao fakeExchangeRateDao, FakeCurrencyDao fakeCurrencyDao) {
        this.fakeExchangeRateDao = fakeExchangeRateDao;
        this.fakeCurrencyDao = fakeCurrencyDao;
    }

    public void fillCurrencies() throws DuplicateEntityException {
        fakeCurrencyDao.create(euroWithoutId);
        fakeCurrencyDao.create(albanianLekWithoutId);
        fakeCurrencyDao.create(dollarWithoutId);
        fakeCurrencyDao.create(bruneiDollarWithoutId);
        fakeCurrencyDao.create(brazilianRealWithoutId);
        fakeCurrencyDao.create(chadWithoutId);
        fakeCurrencyDao.create(chileWithoutId);
        fakeCurrencyDao.create(chileanPesoWithoutId);
        fakeCurrencyDao.create(yuanRenminbiWithoutId);
    }

    public void fillExchangeRates() throws DuplicateEntityException, EntityNotFoundException {
        final ExchangeRateEntity eurToAllBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(euroWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(albanianLekWithoutId.getCode()),
                10
        );
        fakeExchangeRateDao.create(eurToAllBeforeSave);

        final ExchangeRateEntity eurToUsdBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(euroWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(dollarWithoutId.getCode()),
                20
        );
        fakeExchangeRateDao.create(eurToUsdBeforeSave);

        final ExchangeRateEntity eurToBndBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(euroWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(bruneiDollarWithoutId.getCode()),
                30
        );
        fakeExchangeRateDao.create(eurToBndBeforeSave);

        final ExchangeRateEntity allToUsdBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(albanianLekWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(dollarWithoutId.getCode()),
                2
        );

        fakeExchangeRateDao.create(allToUsdBeforeSave);

        final ExchangeRateEntity usdToBrlBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(dollarWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(brazilianRealWithoutId.getCode()),
                6
        );
        fakeExchangeRateDao.create(usdToBrlBeforeSave);

        fillForSameQuotationCrossRate();
    }

    private void fillForSameQuotationCrossRate() throws CurrencyNotFoundException, DuplicateEntityException {
        final ExchangeRateEntity chadToDollarBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(chadWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(dollarWithoutId.getCode()),
                6
        );

        fakeExchangeRateDao.create(chadToDollarBeforeSave);
        final ExchangeRateEntity chileToDollarBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(chileWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(dollarWithoutId.getCode()),
                10
        );
        fakeExchangeRateDao.create(chileToDollarBeforeSave);

        final ExchangeRateEntity dollarToChilleanPesoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(dollarWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(chileanPesoWithoutId.getCode()),
                6
        );

        fakeExchangeRateDao.create(dollarToChilleanPesoBeforeSave);
        final ExchangeRateEntity usdToYuanRenminbiBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(dollarWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(yuanRenminbiWithoutId.getCode()),
                10
        );
        fakeExchangeRateDao.create(usdToYuanRenminbiBeforeSave);
    }
}
