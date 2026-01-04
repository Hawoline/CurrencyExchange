package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.dao.ExchangeRateGetSpecification;
import ru.hawoline.currencyexchange.domain.dao.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dao.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.dao.dto.ExchangeRateDto;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService implements Service<AddExchangeRateDto, ExchangeRateDto> {
    private Dao<ExchangeRateDto, ExchangeRateGetSpecification> exchangeRateDao;
    private Dao<CurrencyDto, String> currencyDao;
    private List<ExchangeRateDto> exchangeRates = new ArrayList<>();

    public ExchangeRateService(Dao<ExchangeRateDto, ExchangeRateGetSpecification> exchangeRateDao,
                               Dao<CurrencyDto, String> currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    @Override
    public void add(AddExchangeRateDto addExchangeRateDto) {
        CurrencyDto baseCurrencyDto = currencyDao.getBySpecification(addExchangeRateDto.baseCurrencyCode());
        CurrencyDto targetCurrencyDto = currencyDao.getBySpecification(addExchangeRateDto.targetCurrencyCode());
        ExchangeRateDto withSavedId = exchangeRateDao.save(new ExchangeRateDto(
                -1, baseCurrencyDto, targetCurrencyDto, addExchangeRateDto.rate()
        ));

        exchangeRates.add(withSavedId);
    }

    @Override
    public ExchangeRateDto getLastAdded() {
        return exchangeRates.getLast();
    }

    @Override
    public void remove(AddExchangeRateDto entity) {

    }

    @Override
    public void update(AddExchangeRateDto entity) {

    }
}
