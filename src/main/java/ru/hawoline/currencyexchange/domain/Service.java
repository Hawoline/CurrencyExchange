package ru.hawoline.currencyexchange.domain;

public interface Service<T, V> {
    V add(T entity);

    void remove(T entity);

    void update(T entity);
}
