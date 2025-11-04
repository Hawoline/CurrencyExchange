package ru.hawoline.currencyexchange.domain;

import java.util.List;

public interface Dao<T> {
    void save(T t);
    T get(int id);

    List<T> getAll();

    void delete(T t);
}