package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.data.repository.CurrencyDao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateInsertEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateRequestBody;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateResponse;
import ru.hawoline.currencyexchange.domain.repository.DataSource;

public class ExchangeRateService implements Service<ExchangeRateRequestBody, ExchangeRateResponse> {
    private DataSource<ExchangeRateInsertEntity, Long> localStorage;
    private CurrencyDao currencyDao = new CurrencyDao();

    public ExchangeRateService(DataSource<ExchangeRateInsertEntity, Long> localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public ExchangeRateResponse add(ExchangeRateRequestBody entity) {
        CurrencyEntity baseCurrencyEntity = currencyDao.get(entity.getBaseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.get(entity.getTargetCurrencyCode());
        long savedId = localStorage.save(new ExchangeRateInsertEntity(
                baseCurrencyEntity.getId(), targetCurrencyEntity.getId(), entity.getRate()
        ));
        return new ExchangeRateResponse(savedId, baseCurrencyEntity, targetCurrencyEntity, entity.getRate());
    }

    @Override
    public void remove(ExchangeRateRequestBody entity) {

    }

    @Override
    public void update(ExchangeRateRequestBody entity) {

    }
}
