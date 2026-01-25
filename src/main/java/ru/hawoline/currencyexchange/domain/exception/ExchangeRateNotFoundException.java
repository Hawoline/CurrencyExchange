package ru.hawoline.currencyexchange.domain.exception;

public class ExchangeRateNotFoundException extends EntityNotFoundException {
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
