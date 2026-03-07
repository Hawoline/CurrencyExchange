package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.data.servlet.QueryParameterParser;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.util.*;

public class CurrencyMapper {
    public CurrencyEntity fromQueryToCurrencyEntity(String query) {
        QueryParameterParser queryParameterParser = new QueryParameterParser();
        Map<String, List<String>> parsed = queryParameterParser.parseQueryParameters(query);
        String code = parsed.get("code").getFirst();
        Currency currency = Currency.getInstance(code);

        return new CurrencyEntity(currency.getDisplayName(Locale.ENGLISH), currency.getCurrencyCode(), currency.getSymbol(Locale.ENGLISH));
    }

    public Currency fromEntityToModel(CurrencyEntity currencyEntity) {
        return Currency.getInstance(currencyEntity.getCode());
    }

    public CurrencyEntity fromModelToEntity(Currency currency) {
        return new CurrencyEntity(currency.getDisplayName(Locale.ENGLISH), currency.getCurrencyCode(), currency.getSymbol());
    }
}
