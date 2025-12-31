package ru.hawoline.currencyexchange.data;

import ru.hawoline.currencyexchange.domain.dao.entity.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.Validator;

public class ExchangeRateRequestBodyValidator implements Validator<AddExchangeRateDto> {
    @Override
    public boolean validate(AddExchangeRateDto addExchangeRateDto) {
        boolean baseCurrencyCodeValid = validateCurrencyCode(addExchangeRateDto.getBaseCurrencyCode());
        boolean targetCurrencyCodeValid = validateCurrencyCode(addExchangeRateDto.getTargetCurrencyCode());
        boolean rateValid = addExchangeRateDto.getRate() > 0;
        return baseCurrencyCodeValid && targetCurrencyCodeValid && rateValid;
    }

    private boolean validateCurrencyCode(String targetCurrencyCode) {
        int validCurrencyCodeLength = 3;
        return targetCurrencyCode.length() == validCurrencyCodeLength;
    }
}
