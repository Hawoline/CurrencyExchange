package ru.hawoline.currencyexchange.domain.dto;

import java.util.Objects;

public class ExchangeRateDto {
    private final long id;
    private final CurrencyDto baseCurrency;
    private final CurrencyDto targetCurrency;
    private final double rate;

    public ExchangeRateDto(long id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public long getId() {
        return id;
    }

    public CurrencyDto getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDto getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

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
