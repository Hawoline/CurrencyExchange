package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.CurrencyIdPair;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeExchangeRateDao implements Dao<ExchangeRateEntity, CurrencyIdPair> {
    private final List<ExchangeRateEntity> exchangeRateEntities = new ArrayList<>();

    @Override
    public ExchangeRateEntity create(ExchangeRateEntity exchangeRateEntity) throws DuplicateEntityException {
        if (exists(exchangeRateEntity)) {
            throw new DuplicateEntityException();
        }
        ExchangeRateEntity saved = new ExchangeRateEntity(exchangeRateEntities.size(),
                exchangeRateEntity.baseCurrencyId(),
                exchangeRateEntity.targetCurrencyId(),
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
    public ExchangeRateEntity getEntityById(CurrencyIdPair id) throws ExchangeRateNotFoundException {
        for (ExchangeRateEntity exchangeRateEntity : exchangeRateEntities) {
            if (exchangeRateEntity.baseCurrencyId() == id.baseCurrencyId() &&
                    exchangeRateEntity.targetCurrencyId() == id.targetCurrencyCode()) {
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
    public void update(ExchangeRateEntity entity) throws ExchangeRateNotFoundException {
        int index = exchangeRateEntities.indexOf(entity);
        ExchangeRateEntity onlyRateUpdated = new ExchangeRateEntity(
                entity.id(),
                entity.baseCurrencyId(),
                entity.targetCurrencyId(),
                entity.rate()
        );
        try {
            exchangeRateEntities.set(index, onlyRateUpdated);
        } catch (IndexOutOfBoundsException e) {
            throw new ExchangeRateNotFoundException("Exchange rate not found");
        }
    }

    @Override
    public ExchangeRateEntity getByIntId(int id) throws EntityNotFoundException {
        return exchangeRateEntities.get(id);
    }

    @Override
    public void delete(CurrencyIdPair id) {

    }
}
