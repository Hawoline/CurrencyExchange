package ru.hawoline.currencyexchange.data.entity;

import ru.hawoline.currencyexchange.domain.Currency;

public class ExchangeRateEntity {
    private final int id;
    private final CurrencyEntity baseCurrency;
    private final CurrencyEntity targetCurrency;
    private final double rate;

    public ExchangeRateEntity(int id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
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
}
