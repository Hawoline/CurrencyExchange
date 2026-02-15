package ru.hawoline.currencyexchange.domain.mapper;

import ru.hawoline.currencyexchange.domain.ExchangeRate;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;

import java.util.Currency;

public class ExchangeRateMapper {
    private CurrencyMapper currencyMapper = new CurrencyMapper();

    public ExchangeRate fromExchangeRateDto(ExchangeRateDto exchangeRateDto) {
        return new ExchangeRate(
                currencyMapper.fromCurrencyDto(exchangeRateDto.getBaseCurrency()),
                currencyMapper.fromCurrencyDto(exchangeRateDto.getTargetCurrency()),
                exchangeRateDto.getRate()
        );
    }

    public ConvertedExchangeRateDto toConvertedExchangeRateDto(double amount, ExchangeRateDto exchangeRateDto, double convertedAmount) {
        return new ConvertedExchangeRateDto(
                exchangeRateDto.getBaseCurrency(),
                exchangeRateDto.getTargetCurrency(),
                exchangeRateDto.getRate(),
                amount,
                convertedAmount
        );
    }

    public ExchangeRateDto toExchangeRateDto(ExchangeRateEntity exchangeRateEntity, CurrencyEntity baseCurrencyEntity,
                                             CurrencyEntity targetCurrencyEntity) {
        return new ExchangeRateDto(
                exchangeRateEntity.id(),
                baseCurrencyEntity,
                targetCurrencyEntity,
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
