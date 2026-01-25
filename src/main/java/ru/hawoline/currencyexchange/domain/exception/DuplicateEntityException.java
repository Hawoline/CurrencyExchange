package ru.hawoline.currencyexchange.domain.exception;

public class DuplicateEntityException extends Exception {
    public DuplicateEntityException() {
        super("Value exists in db");
    }
}
