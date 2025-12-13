package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.entity.ExchangeRateRequestBody;

public class ExchangeRateParser {
    public ExchangeRateRequestBody parseRequestBody(String requestURI) {
        /**
         * Пример body:
         * baseCurrencyCode=AAA&targetCurrencyCode=AAB&rate=0.9
         */
        String[] pairs =  requestURI.split("&"); // baseCurrencyCode=AAA ...
        String baseCurrencyCode = "";
        String targetCurrencyCode = "";
        double rate = 0;
        for (String pair : pairs) {
            String[] pairSplit = pair.split("=");
            String key = pairSplit[0];
            String value = pairSplit[1];
            switch (key) {
                case "baseCurrencyCode" -> baseCurrencyCode = value;
                case "targetCurrencyCode" -> targetCurrencyCode = value;
                case "rate" -> rate = Double.parseDouble(value);
            }
        }

        return new ExchangeRateRequestBody(baseCurrencyCode, targetCurrencyCode, rate);
    }
}
