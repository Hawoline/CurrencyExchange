package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.util.Currency;
import java.util.Locale;

public class CurrencyMapper {
    public CurrencyEntity fromXWwwFormUrlEncoded(String wwwFormUrlEncodedCurrencyEntity) {
        String[] pairs = wwwFormUrlEncodedCurrencyEntity.split("&");
        String name = "";
        String code = "";
        String sign = "";
        for (String pairString : pairs) {
            String[] pair = pairString.split("=");
            String key = pair[0];
            String value = pair[1];
            switch (key) {
                case "name" -> name = value;
                case "code" -> code = value;
                case "sign" -> sign = value;
            }
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is wrong");
        }
        if (code.isEmpty()) {
            throw new IllegalArgumentException("code is wrong");
        }
        if (sign.isEmpty()) {
            throw new IllegalArgumentException("sign is wrong");
        }

        return new CurrencyEntity(name, code, sign);
    }

    public Currency fromEntityToModel(CurrencyEntity currencyEntity) {
        return Currency.getInstance(currencyEntity.getCode());
    }

    public CurrencyEntity fromModelToEntity(Currency currency) {
        return new CurrencyEntity(currency.getDisplayName(Locale.ENGLISH), currency.getCurrencyCode(), currency.getSymbol());
    }
}
