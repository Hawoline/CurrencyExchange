package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import java.util.List;

public interface Dao<T, V> {
    T save(T t) throws DuplicateValueInDbException, ValueNotFoundException;

    T getByLongId(long id) throws ValueNotFoundException;

    T getBy(V id) throws ValueNotFoundException;

    List<T> getAll();

    void update(T object, V id) throws ValueNotFoundException;
}