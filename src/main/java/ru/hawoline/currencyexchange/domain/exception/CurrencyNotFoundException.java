package ru.hawoline.currencyexchange.domain.exception;

public class CurrencyNotFoundException extends ValueNotFoundException {
    public CurrencyNotFoundException(String message) {
        super("Currency with code: " + message + " does not exists");
    }
}
