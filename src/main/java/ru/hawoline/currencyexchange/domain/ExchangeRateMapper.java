package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;

import java.util.Currency;

public class ExchangeRateMapper {
    private CurrencyMapper currencyMapper = new CurrencyMapper();

    public ExchangeRate fromExchangeRateDto(ExchangeRateDto exchangeRateDto) {
        return new ExchangeRate(
                currencyMapper.fromEntityToModel(exchangeRateDto.baseCurrency()),
                currencyMapper.fromEntityToModel(exchangeRateDto.targetCurrency()),
                exchangeRateDto.rate()
        );
    }

    public ConvertedExchangeRateDto toConvertedExchangeRateDto(ConvertedExchangeRate convertedExchangeRate,
                                                               CurrencyEntity base,
                                                               CurrencyEntity target,
                                                               double amount) {
        return new ConvertedExchangeRateDto(
                base,
                target,
                convertedExchangeRate.rate(),
                amount,
                convertedExchangeRate.targetAmount()
        );
    }

    public ExchangeRateDto toExchangeRateDto(ExchangeRateEntity exchangeRateEntity) {
        return new ExchangeRateDto(
                exchangeRateEntity.id(),
                exchangeRateEntity.baseCurrency(),
                exchangeRateEntity.targetCurrency(),
                exchangeRateEntity.rate()
        );
    }

    public ExchangeRate fromExchangeRateEntityToExchangeRate(ExchangeRateEntity exchangeRateEntity) {
        return new ExchangeRate(
                Currency.getInstance(exchangeRateEntity.baseCurrency().getCode()),
                Currency.getInstance(exchangeRateEntity.targetCurrency().getCode()),
                exchangeRateEntity.rate()
        );
    }
}
