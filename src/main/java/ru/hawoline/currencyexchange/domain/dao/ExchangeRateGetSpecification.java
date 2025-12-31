package ru.hawoline.currencyexchange.domain.dao;

public class ExchangeRateGetSpecification {
    private final String baseCurrencyCode;
    private final String targetCurrencyCode;

    public ExchangeRateGetSpecification(String baseCurrencyCode, String targetCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }
}
