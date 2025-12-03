package ru.hawoline.currencyexchange.data.entity;

import ru.hawoline.currencyexchange.domain.ExchangeRate;

import java.util.Currency;

public class ExchangeRateMapper {
    public ExchangeRate fromXWwwFormUrlEncoded(String xWwwFormUrlEncodedString) {
        String[] pairs =  xWwwFormUrlEncodedString.split("&");
        return new ExchangeRate(
                Currency.getInstance("ASD"),
                Currency.getInstance("USD"),
                0.9
        );
    }
}
