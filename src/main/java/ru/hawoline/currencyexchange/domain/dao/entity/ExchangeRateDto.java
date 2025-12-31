package ru.hawoline.currencyexchange.domain.dao.entity;

public class ExchangeRateDto {
    private final long id;
    private final CurrencyEntity baseCurrency;
    private final CurrencyEntity targetCurrency;
    private final double rate;

    public ExchangeRateDto(long id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, double rate) {
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
