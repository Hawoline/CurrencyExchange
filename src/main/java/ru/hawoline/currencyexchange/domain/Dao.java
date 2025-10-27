package ru.hawoline.currencyexchange.domain;

import java.util.List;

public interface Dao<T, V> {
    void save(T t);
    T get(V v);

    boolean exists(V v);
    List<T> getAll();

    void delete(T t);
}