package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.data.dao.CurrencyDao;// TODO убрать
import ru.hawoline.currencyexchange.domain.dao.entity.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dao.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.dao.entity.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dao.entity.ExchangeRateInsertEntity;
import ru.hawoline.currencyexchange.domain.dao.DataSource;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService implements Service<AddExchangeRateDto, ExchangeRateDto> {
    private DataSource<ExchangeRateInsertEntity, Long> localStorage;// TODO переименовать или заменить на dao
    private CurrencyDao currencyDao = new CurrencyDao();
    private List<ExchangeRateDto> exchangeRates = new ArrayList<>();

    public ExchangeRateService(DataSource<ExchangeRateInsertEntity, Long> localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public void add(AddExchangeRateDto entity) {
        CurrencyEntity baseCurrencyEntity = currencyDao.getBySpecification(entity.getBaseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.getBySpecification(entity.getTargetCurrencyCode());
        long savedId = localStorage.save(new ExchangeRateInsertEntity(
                baseCurrencyEntity.getId(), targetCurrencyEntity.getId(), entity.getRate()
        ));

        exchangeRates.add(new ExchangeRateDto(savedId, baseCurrencyEntity, targetCurrencyEntity, entity.getRate()));
    }

    @Override
    public ExchangeRateDto get() {
        return exchangeRates.getLast();
    }

    @Override
    public void remove(AddExchangeRateDto entity) {

    }

    @Override
    public void update(AddExchangeRateDto entity) {

    }
}
