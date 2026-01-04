package ru.hawoline.currencyexchange.domain.dao.dto;

public record AddExchangeRateDto(String baseCurrencyCode, String targetCurrencyCode, double rate) {
}
