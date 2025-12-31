package ru.hawoline.currencyexchange.domain.dao.entity;

public class ExchangeRateInsertEntity {
    private int baseCurrencyId;
    private int targetCurrencyId;
    private double rate;

    public ExchangeRateInsertEntity(int baseCurrencyId, int targetCurrencyId, double rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
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
}
