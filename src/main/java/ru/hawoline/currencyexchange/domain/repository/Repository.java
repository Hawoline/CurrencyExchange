package ru.hawoline.currencyexchange.domain.repository;

import java.util.List;

public interface Repository<T, V> {
    V add(T entity);

    void remove(T entity);

    void update(T entity);

    List<T> query(Specification<T> specification);
}
