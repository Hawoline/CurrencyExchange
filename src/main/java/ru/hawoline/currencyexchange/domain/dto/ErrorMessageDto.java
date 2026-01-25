package ru.hawoline.currencyexchange.domain.dto;

public record ErrorMessageDto(String message) {
    @Override
    public String toString() {
        return "{" +
                "\"message\": \"" + message + "\"" +
                '}';
    }
}
