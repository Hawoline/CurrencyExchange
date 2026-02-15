package ru.hawoline.currencyexchange.domain.service;

import ru.hawoline.currencyexchange.domain.CurrencyPair;
import ru.hawoline.currencyexchange.domain.ExchangeRate;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.mapper.ExchangeRateMapper;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private Dao<ExchangeRateEntity, CurrencyPair> exchangeRateDao;
    private Dao<CurrencyEntity, String> currencyDao;
    private List<ExchangeRateEntity> exchangeRates = new ArrayList<>();
    private ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    public ExchangeRateService(Dao<ExchangeRateEntity, CurrencyPair> exchangeRateDao,
                               Dao<CurrencyEntity, String> currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public void add(AddExchangeRateDto addExchangeRateDto) throws DuplicateEntityException, EntityNotFoundException {
        CurrencyEntity baseCurrencyEntity = currencyDao.getEntityBy(addExchangeRateDto.baseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.getEntityBy(addExchangeRateDto.targetCurrencyCode());
        ExchangeRateEntity beforeSave = new ExchangeRateEntity(
                -1, baseCurrencyEntity, targetCurrencyEntity, addExchangeRateDto.rate()
        );
        ExchangeRateEntity withSavedId = exchangeRateDao.create(beforeSave);

        exchangeRates.add(withSavedId);
    }

    public ExchangeRateDto getLastAdded() throws EntityNotFoundException {
        ExchangeRateEntity last = exchangeRates.getLast();
        return exchangeRateMapper.toExchangeRateDto(last,
                currencyDao.getByIntId(last.baseCurrency().getId()),
                currencyDao.getByIntId(last.targetCurrency().getId())
        );
    }

    public ConvertedExchangeRateDto convert(ExchangeDto exchangeDto) throws EntityNotFoundException{
        CurrencyEntity baseCurrencyEntity = currencyDao.getEntityBy(exchangeDto.from());
        CurrencyEntity targetCurrencyEntity = currencyDao.getEntityBy(exchangeDto.to());

        double amount = exchangeDto.amount();
        try {
            return convertAtDirectRate(amount, baseCurrencyEntity, targetCurrencyEntity);
        } catch (EntityNotFoundException ignored) {

        }
        try {
            return convertAtReverseRate(amount, targetCurrencyEntity, baseCurrencyEntity);
        } catch (EntityNotFoundException ignored) {

        }
        try {
            return convertAtCrossRate(amount, baseCurrencyEntity, targetCurrencyEntity);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ConvertedExchangeRateDto convertAtDirectRate(double amount, CurrencyEntity base, CurrencyEntity target) throws EntityNotFoundException {
        ExchangeRateEntity exchangeRateEntity = exchangeRateDao.getEntityBy(new CurrencyPair(
                base, target)
        );
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                exchangeRateEntity.id(),
                base,
                target,
                exchangeRateEntity.rate()
        );
        ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(exchangeRateDto);
        double convertedAmount = exchangeRate.exchangeToTarget(amount);
        return exchangeRateMapper.toConvertedExchangeRateDto(amount, exchangeRateDto, convertedAmount);
    }

    private ConvertedExchangeRateDto convertAtReverseRate(double amount,
                                                          CurrencyEntity target,
                                                          CurrencyEntity base
    ) throws EntityNotFoundException {
        ExchangeRateEntity exchangeRateEntity = exchangeRateDao.getEntityBy(new CurrencyPair(target,
                base));
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                exchangeRateEntity.id(),
                base,
                target,
                exchangeRateEntity.rate()
        );
        ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(exchangeRateDto);
        final int HUNDREDTHS = 2;
        double convertedAmount = exchangeRate.exchangeToBase(amount, HUNDREDTHS);
        return exchangeRateMapper.toConvertedExchangeRateDto(amount, exchangeRateDto, convertedAmount);
    }

    private ConvertedExchangeRateDto convertAtCrossRate(double amount, CurrencyEntity base, CurrencyEntity target) throws EntityNotFoundException {
        final String usdCurrencyCode = "USD";
        CurrencyEntity dollar = currencyDao.getEntityBy(usdCurrencyCode);
        try {
            ConvertedExchangeRateDto fromDollarToTarget = convertDirectCrossRate(amount, base, target, dollar);
            return fromDollarToTarget;
        } catch (EntityNotFoundException ignored) {

        }

        try {
            ConvertedExchangeRateDto fromDollarToBase = convertReverseCrossRate(amount, base, target, dollar);
            return fromDollarToBase;
        } catch (EntityNotFoundException ignored) {

        }

        ConvertedExchangeRateDto convertedRate = convertCrossRateIfDollarInSameQuotations(amount, base, target, dollar);

        return convertedRate;
    }

    private ConvertedExchangeRateDto convertCrossRateIfDollarInSameQuotations(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        double convertedRate;
        try {
            convertedRate = calculateAndGetCrossRateIfDollarInTargets(amount, base, target, dollar);
        } catch (EntityNotFoundException ignored) {
            convertedRate = calculateAndGetCrossRateIfDollarInBases(amount, base, target, dollar);
        }

        return new ConvertedExchangeRateDto(base, target, convertedRate, amount, convertedRate * amount);
    }

    private ConvertedExchangeRateDto convertDirectCrossRate(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        ConvertedExchangeRateDto fromBaseToDollar = convertAtDirectRate(amount, base, dollar);
        ConvertedExchangeRateDto fromDollarToTarget = convertAtDirectRate(fromBaseToDollar.convertedAmount(), dollar, target);
        return fromDollarToTarget;
    }

    private ConvertedExchangeRateDto convertReverseCrossRate(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        ConvertedExchangeRateDto fromTargetToDollar = convertAtReverseRate(amount, target, dollar);
        ConvertedExchangeRateDto fromDollarToBase = convertAtReverseRate(fromTargetToDollar.convertedAmount(), dollar, base);
        return fromDollarToBase;
    }

    private double calculateAndGetCrossRateIfDollarInTargets(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        ConvertedExchangeRateDto fromBaseToDollar = convertAtDirectRate(amount, base, dollar);
        ConvertedExchangeRateDto fromTargetToDollar = convertAtDirectRate(amount, target, dollar);
        return fromBaseToDollar.convertedAmount() / fromTargetToDollar.convertedAmount();
    }

    private double calculateAndGetCrossRateIfDollarInBases(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        ConvertedExchangeRateDto fromDollarToBase = convertAtDirectRate(amount, dollar, base);
        ConvertedExchangeRateDto fromDollarToTarget = convertAtDirectRate(amount, dollar, target);
        return fromDollarToBase.convertedAmount() / fromDollarToTarget.convertedAmount();
    }

    public List<ExchangeRateDto> getAll() {
        return exchangeRateDao.getAll().stream().map(exchangeRateEntity -> {

            CurrencyEntity baseCurrencyEntity;
            CurrencyEntity targetCurrencyEntity;
            List<CurrencyEntity> currencyEntities = currencyDao.getAll();
            baseCurrencyEntity = currencyEntities.stream().filter(
                    currencyEntity -> currencyEntity.getId() == exchangeRateEntity.baseCurrency().getId()
            ).findFirst().orElseThrow();
            targetCurrencyEntity = currencyEntities.stream().filter(
                    currencyEntity -> currencyEntity.getId() == exchangeRateEntity.targetCurrency().getId()
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
