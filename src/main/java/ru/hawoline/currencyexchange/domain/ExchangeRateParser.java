package ru.hawoline.currencyexchange.domain;


import ru.hawoline.currencyexchange.domain.dao.dto.AddExchangeRateDto;

public class ExchangeRateParser {
    public AddExchangeRateDto parseRequestBody(String requestUri) {
        String[] pairs = requestUri.split("&");
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

        return new AddExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);
    }
}
