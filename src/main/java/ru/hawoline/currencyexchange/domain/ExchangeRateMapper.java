package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.data.CurrencyMapper;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
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
                exchangeDto.getAmount(),
                convertedAmount
        );
    }
}
