package ru.hawoline.currencyexchange.domain.exception;

public class DuplicateEntityException extends Exception {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
