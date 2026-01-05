package ru.hawoline.currencyexchange.domain.dao;

import java.util.List;

public interface Dao<T, V> {
    T save(T t);
    T getByLongId(long id);
    T getBy(V id);
    List<T> getAll();

    void delete(T t);
}