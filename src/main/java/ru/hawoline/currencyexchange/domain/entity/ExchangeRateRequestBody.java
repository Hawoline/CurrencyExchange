package ru.hawoline.currencyexchange.domain.entity;

public class ExchangeRateRequestBody {
    private final String baseCurrencyCode;
    private final String targetCurrencyCode;
    private final double rate;

    public ExchangeRateRequestBody(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public double getRate() {
        return rate;
    }
}
