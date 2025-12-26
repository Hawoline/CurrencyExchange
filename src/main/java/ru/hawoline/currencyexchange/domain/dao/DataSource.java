package ru.hawoline.currencyexchange.domain.dao;

public interface DataSource<T, V> {
    V save(T entity);

    T load(V id);
}
