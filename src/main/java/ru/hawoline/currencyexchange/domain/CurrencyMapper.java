package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.data.servlet.QueryParameterParser;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.util.*;

public class CurrencyMapper {
    public CurrencyEntity fromQueryToCurrencyEntity(String query) {
        QueryParameterParser queryParameterParser = new QueryParameterParser();
        Map<String, List<String>> parsed = queryParameterParser.parseQueryParameters(query);
        String code = parsed.get("code").getFirst();
        String name = parsed.get("name").getFirst();
        String sign = parsed.get("sign").getFirst();
        if (code.isEmpty()) {
            throw new IllegalArgumentException("code is empty");
        }
        if (code.length() != 3) {
            throw new IllegalArgumentException("Code length must be 3 symbols");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        if (sign.isEmpty()) {
            throw new IllegalArgumentException("sign is empty");
        }

        return new CurrencyEntity(sign, code, sign);
    }

    public Currency fromEntityToModel(CurrencyEntity currencyEntity) {
        return Currency.getInstance(currencyEntity.getCode());
    }

    public CurrencyEntity fromModelToEntity(Currency currency) {
        return new CurrencyEntity(currency.getDisplayName(Locale.ENGLISH), currency.getCurrencyCode(), currency.getSymbol());
    }
}
