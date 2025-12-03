package ru.hawoline.currencyexchange.data;

import ru.hawoline.currencyexchange.domain.entity.ExchangeRateRequestBody;
import ru.hawoline.currencyexchange.domain.Validator;

public class ExchangeRateRequestBodyValidator implements Validator<ExchangeRateRequestBody> {
    @Override
    public boolean validate(ExchangeRateRequestBody exchangeRateRequestBody) {
        boolean baseCurrencyCodeValid = validateCurrencyCode(exchangeRateRequestBody.getBaseCurrencyCode());
        boolean targetCurrencyCodeValid = validateCurrencyCode(exchangeRateRequestBody.getTargetCurrencyCode());
        boolean rateValid = exchangeRateRequestBody.getRate() > 0;
        return baseCurrencyCodeValid && targetCurrencyCodeValid && rateValid;
    }

    private boolean validateCurrencyCode(String targetCurrencyCode) {
        int validCurrencyCodeLength = 3;
        return targetCurrencyCode.length() == validCurrencyCodeLength;
    }
}
