package ru.hawoline.currencyexchange.data.entity.mapper;

import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.Currency;

public class CurrencyMapper {
    public Currency toCurrency(CurrencyEntity currencyEntity) {
        return new Currency(
                currencyEntity.getName(),
                currencyEntity.getCode(),
                currencyEntity.getSign()
        );
    }
}
