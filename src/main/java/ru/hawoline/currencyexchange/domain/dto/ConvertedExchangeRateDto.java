package ru.hawoline.currencyexchange.domain.dto;

public class ConvertedExchangeRateDto {
    private final CurrencyDto baseCurrency;
    private final CurrencyDto targetCurrency;
    private final double rate;
    private final double amount;
    private final double convertedAmount;

    public ConvertedExchangeRateDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate, double amount, double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }
}
