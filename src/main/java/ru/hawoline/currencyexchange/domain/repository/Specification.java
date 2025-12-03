package ru.hawoline.currencyexchange.domain.repository;

public interface Specification<T> {
    boolean specified(T entity);
}
