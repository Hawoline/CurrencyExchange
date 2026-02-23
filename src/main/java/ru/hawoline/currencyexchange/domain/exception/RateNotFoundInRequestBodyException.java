package ru.hawoline.currencyexchange.domain.exception;

public class RateNotFoundInRequestBodyException extends Exception {
    public RateNotFoundInRequestBodyException(String message) {
        super(message);
    }
}
