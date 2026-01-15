package ru.hawoline.currencyexchange.data;


import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

public class CurrencyMapper {
    public CurrencyDto fromXWwwFormUrlEncoded(String wwwFormUrlEncodedCurrencyEntity) {
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

        return new CurrencyDto(name, code, sign);
    }

    public CurrencyDto fromResultSet(ResultSet resultSet) throws SQLException {
        return new CurrencyDto(
                resultSet.getInt("id"),
                resultSet.getString("FullName"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }

    public Currency fromCurrencyDto(CurrencyDto currencyDto) {
        return Currency.getInstance(currencyDto.getCode());
    }
}
