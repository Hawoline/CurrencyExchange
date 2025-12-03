package ru.hawoline.currencyexchange.data.entity;

public class ExchangeRateHahahEntity {
    private final int id;
    private final int baseCurrencyId;
    private final int targetCurrencyId;
    private final double rate;

    public ExchangeRateHahahEntity(int baseCurrencyId, int targetCurrencyId, double rate) {
        this(-1, baseCurrencyId, targetCurrencyId,rate);
    }

    public ExchangeRateHahahEntity(int id, int baseCurrencyId, int targetCurrencyId, double rate) {
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
    public String toString() {
        return "{\"id\": " + id + "," +
                "\"baseCurrency\": " + baseCurrencyId + "," +
                "\"code\": " + targetCurrencyId + "," +
                "\"rate\": \"" + rate + "\"}";
    }
}
