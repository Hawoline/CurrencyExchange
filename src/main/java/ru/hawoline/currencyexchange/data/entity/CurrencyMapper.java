package ru.hawoline.currencyexchange.data.entity;


import java.util.Currency;

public class CurrencyMapper {
    public CurrencyEntity fromXWwwFormUrlEncoded(String wwwFormUrlEncodedCurrencyEntity) {
        String[] pairs =  wwwFormUrlEncodedCurrencyEntity.split("&");
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
}
