package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.entity.CurrencyPairEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeExchangeRateDao implements Dao<ExchangeRateEntity, CurrencyPairEntity> {
    private final List<ExchangeRateEntity> exchangeRateEntities = new ArrayList<>();

    @Override
    public ExchangeRateEntity create(ExchangeRateEntity exchangeRateEntity) throws DuplicateEntityException {
        if (exists(exchangeRateEntity)) {
            throw new DuplicateEntityException("Exchange Rate with " + exchangeRateEntity + " already exists.");
        }
        ExchangeRateEntity saved = new ExchangeRateEntity(exchangeRateEntities.size(),
                exchangeRateEntity.baseCurrency(),
                exchangeRateEntity.targetCurrency(),
                exchangeRateEntity.rate()
        );
        exchangeRateEntities.add(saved);
        return saved;
    }

    private boolean exists(ExchangeRateEntity exchangeRateBefore) {
        for (ExchangeRateEntity exchangeRateEntity: exchangeRateEntities) {
            if (exchangeRateBefore.equals(exchangeRateEntity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ExchangeRateEntity getEntityBy(CurrencyPairEntity id) throws ExchangeRateNotFoundException {
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntities) {
            if (exchangeRateEntity.baseCurrency() == id.base() &&
                    exchangeRateEntity.targetCurrency() == id.target()) {
                return exchangeRateEntity;
            }
        }
        throw new ExchangeRateNotFoundException("Exchange rate not found");
    }

    @Override
    public List<ExchangeRateEntity> getAll() {
        return new ArrayList<>(exchangeRateEntities);
    }

    @Override
    public void update(ExchangeRateEntity entity) {
        int index = exchangeRateEntities.indexOf(entity);
        ExchangeRateEntity onlyRateUpdated = new ExchangeRateEntity(
                entity.id(),
                entity.baseCurrency(),
                entity.targetCurrency(),
                entity.rate()
        );
        exchangeRateEntities.set(index, onlyRateUpdated);
    }

    @Override
    public ExchangeRateEntity getByIntId(int id) {
        return exchangeRateEntities.get(id);
    }

    @Override
    public void delete(CurrencyPairEntity id) {

    }

    public void removeAll() {
        exchangeRateEntities.clear();
    }
}
