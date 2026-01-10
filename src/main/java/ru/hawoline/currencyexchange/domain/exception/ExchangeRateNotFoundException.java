package ru.hawoline.currencyexchange.domain.exception;

public class ExchangeRateNotFoundException extends ValueNotFoundException {
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
