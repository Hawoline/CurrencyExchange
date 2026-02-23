package ru.hawoline.currencyexchange.domain.entity;

public record CurrencyPairEntity(CurrencyEntity base, CurrencyEntity target) {
}
