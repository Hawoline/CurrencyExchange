package ru.hawoline.currencyexchange.data.repository;

import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.data.repository.storage.ExchangeRateSqlDataSource;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateInsertEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateRequestBody;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateResponse;
import ru.hawoline.currencyexchange.domain.repository.DataSource;
import ru.hawoline.currencyexchange.domain.repository.Repository;
import ru.hawoline.currencyexchange.domain.repository.Specification;

import java.util.ArrayList;
import java.util.List;
public class ExchangeRateRepository implements Repository<ExchangeRateRequestBody, ExchangeRateResponse> {
    public static final String EXCHANGE_RATE_ALREADY_SAVED = "EXCHANGE_RATE_ALREADY_SAVED";
    public static final String ONE_OR_MORE_RATE_NOT_EXISTS = "ONE_OR_MORE_RATE_NOT_EXISTS";
    private ExchangeRateSqlDataSource localStorage;
    private CurrencyDao currencyDao = new CurrencyDao();

    public ExchangeRateRepository(ExchangeRateSqlDataSource localStorage) {
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

    @Override
    public List<ExchangeRateRequestBody> query(Specification<ExchangeRateRequestBody> specification) {
        return List.of();
    }
}
