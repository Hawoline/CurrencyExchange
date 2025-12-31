package ru.hawoline.currencyexchange.domain.dao;

import java.util.List;

public interface Dao<T, V> {
    void save(T t);
    T getById(long id);
    T getBySpecification(V specification);
    List<T> getAll();

    void delete(T t);
}