package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import java.util.List;

public interface Dao<E, K> {
    E create(E entity) throws DuplicateEntityException;

    E getEntityBy(K id) throws EntityNotFoundException;

    List<E> getAll();

    void update(E entity) throws EntityNotFoundException;

    E getByIntId(int id) throws EntityNotFoundException;

    void delete(K id);
}