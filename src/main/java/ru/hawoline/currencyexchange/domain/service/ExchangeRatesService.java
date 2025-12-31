package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.dao.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.dao.entity.ExchangeRateInsertEntity;
import ru.hawoline.currencyexchange.domain.dao.entity.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dao.entity.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dao.DataSource;

public class ExchangeRatesService implements Service<AddExchangeRateDto, ExchangeRateDto> {
    private DataSource<ExchangeRateInsertEntity, Long> exchangeRateDao;
    private Dao<CurrencyEntity, String> currencyDao;
    private ExchangeRateDto lastAddedExchangeRateDto;

    public ExchangeRatesService(DataSource<ExchangeRateInsertEntity, Long> exchangeRateDao, Dao<CurrencyEntity, String> currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    @Override
    public void add(AddExchangeRateDto entity) {
        CurrencyEntity baseCurrencyEntity = currencyDao.getBySpecification(entity.getBaseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.getBySpecification(entity.getTargetCurrencyCode());
        long savedId = exchangeRateDao.save(new ExchangeRateInsertEntity(
                baseCurrencyEntity.getId(), targetCurrencyEntity.getId(), entity.getRate()
        ));
        lastAddedExchangeRateDto = new ExchangeRateDto(savedId, baseCurrencyEntity, targetCurrencyEntity, entity.getRate());
    }

    @Override
    public ExchangeRateDto get() {
        return lastAddedExchangeRateDto;
    }

    @Override
    public void remove(AddExchangeRateDto entity) {

    }

    @Override
    public void update(AddExchangeRateDto entity) {

    }
}
