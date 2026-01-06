package ru.hawoline.currencyexchange.domain;

public class DuplicateValueInDbException extends Exception {
    public DuplicateValueInDbException() {
        super("Value exists in db");
    }
}
