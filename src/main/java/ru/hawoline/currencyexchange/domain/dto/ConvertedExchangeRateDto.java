package ru.hawoline.currencyexchange.domain.dto;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

public record ConvertedExchangeRateDto(CurrencyEntity baseCurrency,
                                       CurrencyEntity targetCurrency,
                                       double rate,
                                       double amount,
                                       double convertedAmount) {
}
