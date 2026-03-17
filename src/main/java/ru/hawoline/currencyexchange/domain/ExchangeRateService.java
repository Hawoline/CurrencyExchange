package ru.hawoline.currencyexchange.domain;

import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.CurrencyPairEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ExchangeRateService {
    private final Dao<ExchangeRateEntity, CurrencyPairEntity> exchangeRateDao;
    private final Dao<CurrencyEntity, String> currencyDao;
    private final List<ExchangeRateEntity> exchangeRates = new ArrayList<>();
    private final ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    public ExchangeRateService(Dao<ExchangeRateEntity, CurrencyPairEntity> exchangeRateDao,
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
        return exchangeRateMapper.toExchangeRateDto(last);
    }

    public ConvertedExchangeRateDto convert(ExchangeDto exchangeDto) throws EntityNotFoundException {
        CurrencyEntity baseCurrencyEntity = currencyDao.getEntityBy(exchangeDto.from());
        CurrencyEntity targetCurrencyEntity = currencyDao.getEntityBy(exchangeDto.to());

        double amount = exchangeDto.amount();
        try {
            return convertAtDirectRate(amount, baseCurrencyEntity, targetCurrencyEntity);
        } catch (EntityNotFoundException ignored) {

        }
        try {
            return convertAtReverseRate(amount, new CurrencyPairEntity(targetCurrencyEntity, baseCurrencyEntity));
        } catch (EntityNotFoundException ignored) {

        }

        return convertAtCrossRate(amount, baseCurrencyEntity, targetCurrencyEntity);
    }

    private ConvertedExchangeRateDto convertAtDirectRate(double amount, CurrencyEntity base, CurrencyEntity target) throws EntityNotFoundException {
        ExchangeRateEntity exchangeRateEntity = exchangeRateDao.getEntityBy(new CurrencyPairEntity(
                base, target)
        );
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                exchangeRateEntity.id(),
                base,
                target,
                exchangeRateEntity.rate()
        );
        ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateDto(exchangeRateDto);
        ConvertedExchangeRate convertedAmount = exchangeRate.exchangeToTarget(amount);
        return exchangeRateMapper.toConvertedExchangeRateDto(amount, convertedAmount);
    }

    private ConvertedExchangeRateDto convertAtReverseRate(double amount, CurrencyPairEntity currencyPairEntity)
            throws EntityNotFoundException {
        ExchangeRateEntity exchangeRateEntity = exchangeRateDao.getEntityBy(currencyPairEntity);
        ExchangeRate exchangeRate = new ExchangeRate(
                Currency.getInstance(currencyPairEntity.base().getCode()),
                Currency.getInstance(currencyPairEntity.target().getCode()),
                exchangeRateEntity.rate()
        );
        ConvertedExchangeRate convertedAmount = exchangeRate.exchangeToBase(amount);
        return exchangeRateMapper.toConvertedExchangeRateDto(amount, convertedAmount);
    }

    private ConvertedExchangeRateDto convertAtCrossRate(double amount, CurrencyEntity base, CurrencyEntity target) throws EntityNotFoundException {
        final String usdCurrencyCode = "USD";
        CurrencyEntity dollar = currencyDao.getEntityBy(usdCurrencyCode);
        try {
            return convertDirectCrossRate(amount, base, target, dollar);
        } catch (EntityNotFoundException ignored) {

        }

        try {
            return convertReverseCrossRate(amount, base, target, dollar);
        } catch (EntityNotFoundException ignored) {

        }

        return convertCrossRateIfDollarInSameQuotations(amount, base, target, dollar);
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
        return new ConvertedExchangeRateDto(base, target, fromBaseToDollar.rate() * fromDollarToTarget.rate(), amount, fromDollarToTarget.convertedAmount());
    }

    private ConvertedExchangeRateDto convertReverseCrossRate(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        ConvertedExchangeRateDto fromTargetToDollar = convertAtReverseRate(amount, new CurrencyPairEntity(target, dollar));
        ConvertedExchangeRateDto fromDollarToBase = convertAtReverseRate(fromTargetToDollar.convertedAmount(), new CurrencyPairEntity(dollar, base));
        return new ConvertedExchangeRateDto(base, target, fromTargetToDollar.rate() * fromDollarToBase.rate(),
                amount,
                fromDollarToBase.convertedAmount()
        );
    }

    private double calculateAndGetCrossRateIfDollarInTargets(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        ConvertedExchangeRateDto fromBaseToDollar = convertAtDirectRate(amount, base, dollar);
        ConvertedExchangeRateDto fromTargetToDollar = convertAtDirectRate(amount, target, dollar);
        return fromBaseToDollar.convertedAmount() / fromTargetToDollar.convertedAmount();
    }

    private double calculateAndGetCrossRateIfDollarInBases(double amount, CurrencyEntity base, CurrencyEntity target, CurrencyEntity dollar) throws EntityNotFoundException {
        ConvertedExchangeRateDto fromDollarToBase = convertAtDirectRate(amount, dollar, base);
        ConvertedExchangeRateDto fromDollarToTarget = convertAtDirectRate(amount, dollar, target);
        return fromDollarToTarget.convertedAmount() / fromDollarToBase.convertedAmount();
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
