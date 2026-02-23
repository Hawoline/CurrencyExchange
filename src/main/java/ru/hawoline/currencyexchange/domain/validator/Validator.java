package ru.hawoline.currencyexchange.domain.validator;

public interface Validator<T> {
    boolean validate(T object);
}
