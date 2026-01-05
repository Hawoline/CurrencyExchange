package ru.hawoline.currencyexchange.domain.service;

public interface Service<T, V> {
    void add(T entity);
    V getLastAdded();

    void remove(T entity);

    void update(T entity);
}
