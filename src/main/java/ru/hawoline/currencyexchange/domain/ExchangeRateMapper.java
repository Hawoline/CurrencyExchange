package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.dao.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;

public class ExchangeRateMapper {
    private CurrencyMapper currencyMapper = new CurrencyMapper();

    public ExchangeRate fromExchangeRateDto(ExchangeRateDto exchangeRateDto) {
        return new ExchangeRate(
                currencyMapper.fromCurrencyDto(exchangeRateDto.getBaseCurrency()),
                currencyMapper.fromCurrencyDto(exchangeRateDto.getTargetCurrency()),
                exchangeRateDto.getRate()
        );
    }

    public ConvertedExchangeRateDto toConvertedExchangeRateDto(ExchangeDto exchangeDto, ExchangeRateDto exchangeRateDto, double convertedAmount) {
        return new ConvertedExchangeRateDto(
                exchangeRateDto.getBaseCurrency(),
                exchangeRateDto.getTargetCurrency(),
                exchangeRateDto.getRate(),
                exchangeDto.amount(),
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
}
