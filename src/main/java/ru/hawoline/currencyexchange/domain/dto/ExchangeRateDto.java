package ru.hawoline.currencyexchange.domain.dto;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.util.Objects;

public record ExchangeRateDto(long id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, double rate) {

    @Override
    public String toString() {

        return "{\"id\": " + id + "," +
                "\"baseCurrency\": " + baseCurrency.toString() + "," +
                "\"targetCurrency\": " + targetCurrency.toString() + "," +
                "\"rate\": \"" + rate + "\"}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateDto that = (ExchangeRateDto) o;
        return Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency);
    }
}
