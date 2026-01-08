package ru.hawoline.currencyexchange.domain.dao.dto;

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
}
