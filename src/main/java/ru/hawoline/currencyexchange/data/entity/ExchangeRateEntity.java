package ru.hawoline.currencyexchange.data.entity;

import ru.hawoline.currencyexchange.domain.Entity;

public class ExchangeRateEntity implements Entity {
    private final int id;
    private final int baseCurrencyId;
    private final int targetCurrencyId;
    private final double rate;

    public ExchangeRateEntity(int baseCurrencyId, int targetCurrencyId, double rate) {
        this(-1, baseCurrencyId, targetCurrencyId,rate);
    }

    public ExchangeRateEntity(int id, int baseCurrencyId, int targetCurrencyId, double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public String toJson() {
        return "{\"id\": " + id + "," +
                "\"baseCurrency\": " + baseCurrencyId + "," +
                "\"code\": " + targetCurrencyId + "," +
                "\"rate\": \"" + rate + "\"}";
    }
}
