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
                currencyMapper.fromEntityToModel(exchangeRateDto.getBaseCurrency()),
                currencyMapper.fromEntityToModel(exchangeRateDto.getTargetCurrency()),
                exchangeRateDto.getRate()
        );
    }

    public ConvertedExchangeRateDto toConvertedExchangeRateDto(double amount, ConvertedExchangeRate convertedExchangeRate) {
        return new ConvertedExchangeRateDto(
                currencyMapper.getCurrencyEntityFrom(convertedExchangeRate.base()),
                currencyMapper.getCurrencyEntityFrom(convertedExchangeRate.target()),
                convertedExchangeRate.rate(),
                amount,
                convertedExchangeRate.targetAmount()
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
