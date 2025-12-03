package ru.hawoline.currencyexchange.domain.entity;

import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;

public class ExchangeRateResponse {
    private final long id;
    private final CurrencyEntity baseCurrency;
    private final CurrencyEntity targetCurrency;
    private final double rate;

    public ExchangeRateResponse(long id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public long getId() {
        return id;
    }

    public CurrencyEntity getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyEntity getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public String toString() {

        return "{\"id\": " + id + "," +
                "\"baseCurrency\": " + baseCurrency.toString() + "," +
                "\"code\": " + targetCurrency.toString() + "," +
                "\"rate\": \"" + rate + "\"}";
    }
}
