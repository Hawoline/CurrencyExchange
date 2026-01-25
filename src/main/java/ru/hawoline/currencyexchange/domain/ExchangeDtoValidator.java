package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;

public class ExchangeDtoValidator implements Validator<ExchangeDto> {
    @Override
    public boolean validate(ExchangeDto exchangeDto) {
        if (exchangeDto.amount() < 0) {
            return false;
        }
        return validateCurrencyCode(exchangeDto.from()) && validateCurrencyCode(exchangeDto.to());
    }

    private boolean validateCurrencyCode(String currencyCode) {
        final int validCurrencyCodeLength = 3;
        return currencyCode.length() == validCurrencyCodeLength;
    }
}
