package ru.hawoline.currencyexchange.domain.dao;

import java.util.Objects;

public record ExchangeRateEntity(int id, int baseCurrencyId, int targetCurrencyId, double rate) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateEntity that = (ExchangeRateEntity) o;
        return baseCurrencyId == that.baseCurrencyId && targetCurrencyId == that.targetCurrencyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrencyId, targetCurrencyId);
    }
}
