package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

public interface Service<T, V> {
    void add(T entity) throws DuplicateValueInDbException, ValueNotFoundException;
    V getLastAdded();

    void remove(T entity);

    void update(T entity);
}
