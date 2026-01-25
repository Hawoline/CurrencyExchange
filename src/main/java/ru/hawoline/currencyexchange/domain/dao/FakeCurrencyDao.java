package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.dto.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeCurrencyDao implements Dao<CurrencyEntity, String> {
    private final List<CurrencyEntity> currencies = new ArrayList<>();

    @Override
    public CurrencyEntity create(CurrencyEntity currencyEntityWithoutId) throws DuplicateEntityException {
        if (exists(currencyEntityWithoutId.getCode())) {
            throw new DuplicateEntityException();
        }

        CurrencyEntity currencyEntityWithId = new CurrencyEntity(
                currencies.size(),
                currencyEntityWithoutId.getName(),
                currencyEntityWithoutId.getCode(),
                currencyEntityWithoutId.getSign()
        );
        currencies.add(currencyEntityWithId);
        return currencyEntityWithId;
    }

    @Override
    public CurrencyEntity getEntityById(String currencyCode) throws CurrencyNotFoundException {
        for (CurrencyEntity currencyEntity : currencies) {
            if (currencyEntity.getCode().equals(currencyCode)) {
                return currencyEntity;
            }
        }

        throw new CurrencyNotFoundException("Currency not found");
    }

    @Override
    public List<CurrencyEntity> getAll() {
        return new ArrayList<>(currencies);
    }

    @Override
    public void update(CurrencyEntity entity) throws CurrencyNotFoundException {
        int indexOfUpdatableCurrency = -1;
        for (CurrencyEntity findableCurrencyEntity : currencies) {
            if (findableCurrencyEntity.getCode().equals(entity.getCode())) {
                indexOfUpdatableCurrency = findableCurrencyEntity.getId();
            }
        }
        if (indexOfUpdatableCurrency == -1) {
            String message = "Currency with same currencyCode not found while updating. Currency code: " + entity.getCode();
            throw new CurrencyNotFoundException(message);
        }
        currencies.set(indexOfUpdatableCurrency, entity);
    }

    @Override
    public CurrencyEntity getByIntId(int id) throws EntityNotFoundException {
        return currencies.get(id);
    }

    private boolean exists(String currencyCode) {
        for (CurrencyEntity currencyEntity : currencies) {
            if (currencyEntity.getCode().equals(currencyCode)) {
                return true;
            }
        }

        return false;
    }
}
