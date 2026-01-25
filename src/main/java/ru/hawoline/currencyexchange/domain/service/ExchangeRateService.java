package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.domain.CurrencyIdPair;
import ru.hawoline.currencyexchange.domain.ExchangeRate;
import ru.hawoline.currencyexchange.domain.ExchangeRateMapper;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.dao.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.dto.*;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private Dao<ExchangeRateEntity, CurrencyIdPair> exchangeRateDao;
    private Dao<CurrencyEntity, String> currencyDao;
    private List<ExchangeRateEntity> exchangeRates = new ArrayList<>();
    private static final String USD = "USD";
    private ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    public ExchangeRateService(Dao<ExchangeRateEntity, CurrencyIdPair> exchangeRateDao,
                               Dao<CurrencyEntity, String> currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public void add(AddExchangeRateDto addExchangeRateDto) throws DuplicateEntityException, EntityNotFoundException {
        CurrencyEntity baseCurrencyEntity = currencyDao.getEntityById(addExchangeRateDto.baseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.getEntityById(addExchangeRateDto.targetCurrencyCode());
        ExchangeRateEntity beforeSave = new ExchangeRateEntity(
                -1, baseCurrencyEntity.getId(), targetCurrencyEntity.getId(), addExchangeRateDto.rate()
        );
        ExchangeRateEntity withSavedId = exchangeRateDao.create(beforeSave);

        exchangeRates.add(withSavedId);
    }

    public ExchangeRateDto getLastAdded() throws EntityNotFoundException {
        ExchangeRateEntity last = exchangeRates.getLast();
        return exchangeRateMapper.toExchangeRateDto(last,
                currencyDao.getByIntId(last.baseCurrencyId()),
                currencyDao.getByIntId(last.targetCurrencyId())
        );
    }

    public ConvertedExchangeRateDto convert(ExchangeDto exchangeDto) throws ExchangeRateNotFoundException {
        CurrencyEntity baseCurrencyEntity;
        CurrencyEntity targetCurrencyEntity;
        try {
            baseCurrencyEntity = currencyDao.getEntityById(exchangeDto.from());
            targetCurrencyEntity = currencyDao.getEntityById(exchangeDto.to());
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        int baseCurrencyEntityId = baseCurrencyEntity.getId();
        int targetCurrencyEntityId = targetCurrencyEntity.getId();
        try {
            ExchangeRateEntity exchangeRateEntity = exchangeRateDao.getEntityById(new CurrencyIdPair(
                    baseCurrencyEntityId, targetCurrencyEntityId)
            );
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                    exchangeRateEntity.id(),
                    baseCurrencyEntity,
                    targetCurrencyEntity,
                    exchangeRateEntity.rate()
            );
            ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(exchangeRateDto);
            double convertedAmount = exchangeRate.exchangeToTarget(exchangeDto.amount());
            return exchangeRateMapper.toConvertedExchangeRateDto(exchangeDto, exchangeRateDto, convertedAmount);
        } catch (EntityNotFoundException ignored) {

        }
        try {
            ExchangeRateEntity exchangeRateEntity = exchangeRateDao.getEntityById(new CurrencyIdPair(targetCurrencyEntityId,
                    baseCurrencyEntityId));
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                    exchangeRateEntity.id(),
                    baseCurrencyEntity,
                    targetCurrencyEntity,
                    exchangeRateEntity.rate()
            );
            ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(exchangeRateDto);
            final int HUNDREDTHS = 2;
            double convertedAmount = exchangeRate.exchangeToBase(exchangeDto.amount(), HUNDREDTHS);
            return exchangeRateMapper.toConvertedExchangeRateDto(exchangeDto, exchangeRateDto, convertedAmount);
        } catch (EntityNotFoundException ignored) {

        }
        try {
            CurrencyEntity dollarEntity = currencyDao.getEntityById(USD);
            int dollarId = dollarEntity.getId();
            ExchangeRateEntity fromUsdToFrom = exchangeRateDao.getEntityById(new CurrencyIdPair(dollarId, baseCurrencyEntityId));
            ExchangeRateEntity fromFromToUsd = exchangeRateDao.getEntityById(new CurrencyIdPair(targetCurrencyEntityId, dollarId));
            ExchangeRateEntity fromUsdToTo = exchangeRateDao.getEntityById(new CurrencyIdPair(dollarId, targetCurrencyEntityId));
            ExchangeRateEntity fromToToUsd = exchangeRateDao.getEntityById(new CurrencyIdPair(targetCurrencyEntityId, dollarId));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        //TODO исправить
        return new ConvertedExchangeRateDto(
                new CurrencyEntity("mock", "mock", "mock"),
                new CurrencyEntity("mock", "mock", "mock"),
                1,
                1,
                1
        );
    }

    public List<ExchangeRateDto> getAll() {
        return exchangeRateDao.getAll().stream().map(exchangeRateEntity -> {

            CurrencyEntity baseCurrencyEntity;
            CurrencyEntity targetCurrencyEntity;
            List<CurrencyEntity> currencyEntities = currencyDao.getAll();
            baseCurrencyEntity = currencyEntities.stream().filter(
                    currencyEntity -> currencyEntity.getId() == exchangeRateEntity.baseCurrencyId()
            ).findFirst().orElseThrow();
            targetCurrencyEntity = currencyEntities.stream().filter(
                    currencyEntity -> currencyEntity.getId() == exchangeRateEntity.targetCurrencyId()
            ).findFirst().orElseThrow();
            return new ExchangeRateDto(
                    exchangeRateEntity.id(),
                    baseCurrencyEntity,
                    targetCurrencyEntity,
                    exchangeRateEntity.rate()
            );
        }).toList();
    }
}
