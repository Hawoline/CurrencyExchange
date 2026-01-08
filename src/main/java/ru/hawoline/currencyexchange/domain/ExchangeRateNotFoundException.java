package ru.hawoline.currencyexchange.domain;

public class ExchangeRateNotFoundException extends ValueNotFoundException {
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
