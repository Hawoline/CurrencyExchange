package ru.hawoline.currencyexchange.domain.entity;

import java.util.Objects;

public record ExchangeRateEntity(int id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, double rate) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateEntity that = (ExchangeRateEntity) o;
        return baseCurrency == that.baseCurrency && targetCurrency == that.targetCurrency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency);
    }
}
