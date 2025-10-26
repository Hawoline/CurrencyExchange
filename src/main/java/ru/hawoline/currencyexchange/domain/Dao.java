package ru.hawoline.currencyexchange.domain;

import java.util.List;

public interface Dao<T> {
    void save(T t);

    boolean exists(T t);
    List<T> getAll();

    void delete(T t);
}