package ru.hawoline.currencyexchange.domain.exception;

public class CurrencyNotFoundException extends EntityNotFoundException {
    public CurrencyNotFoundException(String currencyCode) {
        super("Currency with code " + currencyCode + " does not exists");
    }
}
