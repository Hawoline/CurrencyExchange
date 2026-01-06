package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.domain.DuplicateValueInDbException;

public interface Service<T, V> {
    void add(T entity) throws DuplicateValueInDbException;
    V getLastAdded();

    void remove(T entity);

    void update(T entity);
}
