package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

public record CurrencyPair(CurrencyEntity baseCurrencyId, CurrencyEntity targetCurrencyCode) {
}
