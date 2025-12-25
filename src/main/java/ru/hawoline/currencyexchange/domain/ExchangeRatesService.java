package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.data.repository.CurrencyDao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateInsertEntity;
import ru.hawoline.currencyexchange.domain.entity.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dao.DataSource;

public class ExchangeRatesService implements Service<AddExchangeRateDto, ExchangeRateDto> {
    private DataSource<ExchangeRateInsertEntity, Long> exchangeRateDao;
    private CurrencyDao currencyDao = new CurrencyDao();

    public ExchangeRatesService(DataSource<ExchangeRateInsertEntity, Long> exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    @Override
    public ExchangeRateDto add(AddExchangeRateDto entity) {
        CurrencyEntity baseCurrencyEntity = currencyDao.get(entity.getBaseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.get(entity.getTargetCurrencyCode());
        long savedId = exchangeRateDao.saveAndGetId(new ExchangeRateInsertEntity(
                baseCurrencyEntity.getId(), targetCurrencyEntity.getId(), entity.getRate()
        ));
        return new ExchangeRateDto(savedId, baseCurrencyEntity, targetCurrencyEntity, entity.getRate());
    }

    @Override
    public void remove(AddExchangeRateDto entity) {

    }

    @Override
    public void update(AddExchangeRateDto entity) {

    }
}
