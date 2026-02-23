package ru.hawoline.currencyexchange.domain;

import java.util.Currency;

public record ConvertedExchangeRate(Currency base, Currency target, double rate, double targetAmount) {
}
