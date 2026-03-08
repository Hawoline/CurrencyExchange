package ru.hawoline.currencyexchange.domain;


import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;

import java.util.Map;

public class ExchangeRateParser {
    public AddExchangeRateDto parseAddExchangeRateFrom(Map<String, String[]> parameterMap) {
        String baseCurrencyCode = parameterMap.get("baseCurrencyCode")[0];
        String targetCurrencyCode = parameterMap.get("targetCurrencyCode")[0];
        double rate = Double.parseDouble(parameterMap.get("rate")[0]);
        return new AddExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public ExchangeDto parseExchangeFrom(Map<String, String[]> parameterMap) {
        String from = parameterMap.get("from")[0];
        String to = parameterMap.get("to")[0];
        double amount = Double.parseDouble(parameterMap.get("amount")[0]);
        return new ExchangeDto(from, to, amount);
    }
}
