package ru.hawoline.currencyexchange.domain.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
