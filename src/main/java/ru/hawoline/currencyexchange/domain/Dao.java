package ru.hawoline.currencyexchange.domain;

import java.util.List;

public interface Dao<T> {
    List<T> getAll();
    void save(T t);
    void delete(T t);
}