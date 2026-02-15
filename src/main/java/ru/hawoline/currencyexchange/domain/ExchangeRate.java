package ru.hawoline.currencyexchange.domain;

import java.util.Currency;

public class ExchangeRate {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private double rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public double exchangeToTarget(double baseAmount) {
        return rate * baseAmount;
    }

    public double exchangeToBase(double targetAmount, int roundingTo) {
        return roundAvoid(targetAmount / rate, roundingTo);
    }

    private double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
