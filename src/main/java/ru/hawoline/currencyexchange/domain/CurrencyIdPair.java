package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

public record CurrencyIdPair(CurrencyEntity baseCurrencyId, CurrencyEntity targetCurrencyCode) {
}
