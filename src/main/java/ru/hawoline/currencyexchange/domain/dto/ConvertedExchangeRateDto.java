package ru.hawoline.currencyexchange.domain.dto;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

public class ConvertedExchangeRateDto {
    private final CurrencyEntity baseCurrency;
    private final CurrencyEntity targetCurrency;
    private final double rate;
    private final double amount;
    private final double convertedAmount;

    public ConvertedExchangeRateDto(CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, double rate, double amount, double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }
}
