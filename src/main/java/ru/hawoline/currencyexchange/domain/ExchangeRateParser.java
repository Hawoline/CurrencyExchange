package ru.hawoline.currencyexchange.domain;


import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;
import ru.hawoline.currencyexchange.domain.exception.RateNotFoundInRequestBodyException;

public class ExchangeRateParser {
    public AddExchangeRateDto parseRequestBody(String requestUri) {
        String[] pairs = requestUri.split("&");
        String baseCurrencyCode = "";
        String targetCurrencyCode = "";
        double rate = -1;
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

    public double parseRateFromRequestBody(String requestUri) throws RateNotFoundInRequestBodyException {
        String[] pair = requestUri.split("=");
        if (pair.length < 2) {
            throw new RateNotFoundInRequestBodyException("Parameter \"rate\" not found in Request Body");
        }
        return Double.parseDouble(pair[1]);
    }

    public ExchangeDto parseExchangeQueryString(String queryString) {
        String[] pairs = queryString.split("&");
        String from = "";
        String to = "";
        double amount = -1;
        for (String pair : pairs) {
            String[] pairSplit = pair.split("=");
            String key = pairSplit[0];
            String value = pairSplit[1];
            switch (key) {
                case "from" -> from = value;
                case "to" -> to = value;
                case "amount" -> amount = Double.parseDouble(value);
            }
        }

        return new ExchangeDto(from, to, amount);
    }
}
