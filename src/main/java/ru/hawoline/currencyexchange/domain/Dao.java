package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import java.util.List;

public interface Dao<T, V> {
    T save(T t) throws DuplicateValueInDbException;

    T getByLongId(long id);

    T getBy(V id) throws ValueNotFoundException;

    List<T> getAll();

    void delete(T object);

    void update(T object, V id);
}