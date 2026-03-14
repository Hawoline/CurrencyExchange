package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.util.*;

public class CurrencyMapper {
    public CurrencyEntity getCurrencyEntityFrom(Map<String, String[]> parameterMap) {
        String[] codes = parameterMap.get("code");
        String code = codes[0];
        Currency currency = Currency.getInstance(code);

        return new CurrencyEntity(currency.getDisplayName(Locale.ENGLISH), currency.getCurrencyCode(), currency.getSymbol());
    }

    public CurrencyEntity getCurrencyEntityFrom(Currency currency) {
        return new CurrencyEntity(currency.getDisplayName(Locale.ENGLISH), currency.getCurrencyCode(), currency.getSymbol());
    }

    public Currency fromEntityToModel(CurrencyEntity currencyEntity) {
        return Currency.getInstance(currencyEntity.getCode());
    }
}
