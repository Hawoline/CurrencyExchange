package ru.hawoline.currencyexchange.domain.exception;

public class RateNotFoundExceptionInRequestBody extends Exception {
    public RateNotFoundExceptionInRequestBody(String message) {
        super(message);
    }
}
