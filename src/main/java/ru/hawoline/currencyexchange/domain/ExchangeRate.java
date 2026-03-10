package ru.hawoline.currencyexchange.domain;

import java.util.Currency;

public class ExchangeRate {
    private final Currency base;
    private final Currency target;
    private final double rate;

    public ExchangeRate(Currency base, Currency target, double rate) {
        this.base = base;
        this.target = target;
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public ConvertedExchangeRate exchangeToTarget(double baseAmount) {
        double targetAmount = rate * baseAmount;
        return new ConvertedExchangeRate(base, target, rate, targetAmount);
    }

    public ConvertedExchangeRate exchangeToBase(double targetAmount) {
        double baseAmount = roundAvoid(targetAmount / rate, base.getDefaultFractionDigits());
        return new ConvertedExchangeRate(target, base, 1 / rate, baseAmount);
    }

    private double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
