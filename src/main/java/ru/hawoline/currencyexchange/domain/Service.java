package ru.hawoline.currencyexchange.domain;

public interface Service<T, V> {
    void add(T entity);
    V get();

    void remove(T entity);

    void update(T entity);
}
