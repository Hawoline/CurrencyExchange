package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.DuplicateValueInDbException;

import java.util.List;

public interface Dao<T, V> {
    T save(T t) throws DuplicateValueInDbException;
    T getByLongId(long id);
    T getBy(V id);
    List<T> getAll();

    void delete(T t);
}