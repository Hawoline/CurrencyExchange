package ru.hawoline.currencyexchange.data;


import ru.hawoline.currencyexchange.domain.dao.entity.CurrencyEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyEntityMapper {
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

    public CurrencyEntity fromResultSet(ResultSet resultSet) throws SQLException {
        return new CurrencyEntity(
                resultSet.getInt("id"),
                resultSet.getString("FullName"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }
}
