package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.repository.Specification;

import java.util.List;

public interface Service<T, V> {
    V add(T entity);

    void remove(T entity);

    void update(T entity);
}
