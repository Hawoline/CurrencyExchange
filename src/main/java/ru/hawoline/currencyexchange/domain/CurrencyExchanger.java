package ru.hawoline.currencyexchange.domain;

public class CurrencyExchanger {
    private final Currency baseCurrency;
    private Currency target;
    private double rate;

    public CurrencyExchanger(Currency baseCurrency, Currency target, double rate) {
        this.baseCurrency = baseCurrency;
        this.target = target;
        this.rate = rate;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTarget() {
        return target;
    }

    public double getRate() {
        return rate;
    }

    public double exchangeToTarget(double baseAmount) {
        return baseAmount * rate;
    }

    public double exchangeToBase(double targetAmount, int roundingTo) {
        return roundAvoid(targetAmount / rate, roundingTo);
    }

    private double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
