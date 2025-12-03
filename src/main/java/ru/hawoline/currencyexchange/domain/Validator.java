package ru.hawoline.currencyexchange.domain;

public interface Validator<T> {
    boolean validate(T t);
}
