package ru.hawoline.currencyexchange.domain.exception;

public class ValueNotFoundException extends Exception {
    public ValueNotFoundException(String message) {
        super(message);
    }
}
