package ru.hawoline.currencyexchange.domain.dto;

public record AddExchangeRateDto(String baseCurrencyCode, String targetCurrencyCode, double rate) {
}
