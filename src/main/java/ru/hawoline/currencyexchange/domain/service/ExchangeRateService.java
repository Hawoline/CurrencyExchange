package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.domain.ExchangeRate;
import ru.hawoline.currencyexchange.domain.ExchangeRateId;
import ru.hawoline.currencyexchange.domain.ExchangeRateMapper;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.dto.*;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private Dao<ExchangeRateDto, ExchangeRateId> exchangeRateDao;
    private Dao<CurrencyDto, String> currencyDao;
    private List<ExchangeRateDto> exchangeRates = new ArrayList<>();
    private static final String USD = "USD";
    private ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    public ExchangeRateService(Dao<ExchangeRateDto, ExchangeRateId> exchangeRateDao,
                               Dao<CurrencyDto, String> currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public void add(AddExchangeRateDto addExchangeRateDto) throws DuplicateValueInDbException, ValueNotFoundException {
        CurrencyDto baseCurrencyDto = currencyDao.getBy(addExchangeRateDto.baseCurrencyCode());
        CurrencyDto targetCurrencyDto = currencyDao.getBy(addExchangeRateDto.targetCurrencyCode());
        ExchangeRateDto beforeSave = new ExchangeRateDto(
                -1, baseCurrencyDto, targetCurrencyDto, addExchangeRateDto.rate()
        );
        ExchangeRateDto withSavedId = exchangeRateDao.save(beforeSave);

        exchangeRates.add(withSavedId);
    }

    public ExchangeRateDto getLastAdded() {
        return exchangeRates.getLast();
    }

    public ConvertedExchangeRateDto convert(ExchangeDto exchangeDto) throws ExchangeRateNotFoundException {
        try {
            ExchangeRateDto exchangeRateDto = exchangeRateDao.getBy(new ExchangeRateId(exchangeDto.getFrom(),
                    exchangeDto.getTo()));
            ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(exchangeRateDto);
            double convertedAmount = exchangeRate.exchangeToTarget(exchangeDto.getAmount());
            return exchangeRateMapper.toConvertedExchangeRateDto(exchangeDto, exchangeRateDto, convertedAmount);
        } catch (ValueNotFoundException ignored) {

        }
        try {
            ExchangeRateDto exchangeRateDto = exchangeRateDao.getBy(new ExchangeRateId(exchangeDto.getTo(),
                    exchangeDto.getFrom()));
            ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(exchangeRateDto);
            final int HUNDREDTHS = 2;
            double convertedAmount = exchangeRate.exchangeToBase(exchangeDto.getAmount(), HUNDREDTHS);
            return exchangeRateMapper.toConvertedExchangeRateDto(exchangeDto, exchangeRateDto, convertedAmount);
        } catch (ValueNotFoundException ignored) {

        }
        double amountFromToUsd = convertToUsd(exchangeDto.getFrom(), exchangeDto.getAmount());
        try {
            ExchangeRateDto fromUsdToFrom = exchangeRateDao.getBy(new ExchangeRateId(USD, exchangeDto.getFrom()));
            ExchangeRateDto fromFromToUsd = exchangeRateDao.getBy(new ExchangeRateId(exchangeDto.getFrom(), USD));
            ExchangeRateDto fromUsdToTo = exchangeRateDao.getBy(new ExchangeRateId(USD, exchangeDto.getTo()));
            ExchangeRateDto fromToToUsd = exchangeRateDao.getBy(new ExchangeRateId(exchangeDto.getTo(), USD));
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        //TODO исправить
        return new ConvertedExchangeRateDto(
                new CurrencyDto("mock", "mock", "mock"),
                new CurrencyDto("mock", "mock", "mock"),
                1,
                1,
                1
        );
    }

    private double convertToUsd(String currencyCode, double amount) throws ExchangeRateNotFoundException {
        try {
            ExchangeRateDto fromUsdToFrom = exchangeRateDao.getBy(new ExchangeRateId(USD, currencyCode));
            ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(fromUsdToFrom);
            final int hundredth = 2;
            return exchangeRate.exchangeToBase(amount, hundredth);
        } catch (ValueNotFoundException e) {
            try {
                ExchangeRateDto fromFromToUsd = exchangeRateDao.getBy(new ExchangeRateId(currencyCode, USD));
                ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(fromFromToUsd);
                return exchangeRate.exchangeToTarget(amount);
            } catch (ValueNotFoundException ex) {
                throw new ExchangeRateNotFoundException("Cross exchange rate USD with " + currencyCode + " not found");
            }
        }
    }
}
