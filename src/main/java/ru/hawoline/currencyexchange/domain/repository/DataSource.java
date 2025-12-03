package ru.hawoline.currencyexchange.domain.repository;

public interface DataSource<T, V> {
    V save(T entity);

    T load(V id); // TODO подумать потом как нормально это сделать
}
