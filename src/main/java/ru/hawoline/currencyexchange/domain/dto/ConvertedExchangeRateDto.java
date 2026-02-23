package ru.hawoline.currencyexchange.domain.dto;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

public record ConvertedExchangeRateDto(CurrencyEntity baseCurrency,
                                       CurrencyEntity targetCurrency,
                                       double rate,
                                       double amount,
                                       double convertedAmount) {
    @Override
    public String toString() {
        return String.format(
                """
                {
                    "baseCurrency": %s,
                    "targetCurrency": %s,
                    "rate": %f,
                    "amount": %f,
                    "convertedAmount": %f
                }
                """, baseCurrency.toString(), targetCurrency.toString(), rate, amount, convertedAmount
        );
    }
}
