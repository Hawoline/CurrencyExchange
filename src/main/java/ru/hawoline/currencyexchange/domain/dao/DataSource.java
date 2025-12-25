package ru.hawoline.currencyexchange.domain.dao;

public interface DataSource<T, V> {
    V saveAndGetId(T entity);

    T load(V id);
}
