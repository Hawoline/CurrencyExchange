package ru.hawoline.currencyexchange.data;

import ru.hawoline.currencyexchange.domain.Validator;
import ru.hawoline.currencyexchange.domain.dao.dto.AddExchangeRateDto;

public class ExchangeRateDtoValidator implements Validator<AddExchangeRateDto> {
    @Override
    public boolean validate(AddExchangeRateDto addExchangeRateDto) {
        boolean baseCurrencyCodeValid = validateCurrencyCode(addExchangeRateDto.baseCurrencyCode());
        boolean targetCurrencyCodeValid = validateCurrencyCode(addExchangeRateDto.targetCurrencyCode());
        boolean rateValid = addExchangeRateDto.rate() > 0;
        return baseCurrencyCodeValid && targetCurrencyCodeValid && rateValid;
    }

    private boolean validateCurrencyCode(String targetCurrencyCode) {
        int validCurrencyCodeLength = 3;
        return targetCurrencyCode.length() == validCurrencyCodeLength;
    }
}
