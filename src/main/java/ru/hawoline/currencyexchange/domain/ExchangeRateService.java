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

import java.util.Currency;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final Dao<ExchangeRateEntity, CurrencyPairEntity> exchangeRateDao;
    private final Dao<CurrencyEntity, String> currencyDao;
    private final ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    public ExchangeRateService(Dao<ExchangeRateEntity, CurrencyPairEntity> exchangeRateDao,
                               Dao<CurrencyEntity, String> currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public ExchangeRateDto add(AddExchangeRateDto addExchangeRateDto)
            throws DuplicateEntityException, EntityNotFoundException {
        CurrencyEntity baseCurrencyEntity = currencyDao.getEntityBy(addExchangeRateDto.baseCurrencyCode());
        CurrencyEntity targetCurrencyEntity = currencyDao.getEntityBy(addExchangeRateDto.targetCurrencyCode());
        ExchangeRateEntity beforeSave = new ExchangeRateEntity(
                -1, baseCurrencyEntity, targetCurrencyEntity, addExchangeRateDto.rate()
        );
        ExchangeRateEntity withSavedId = exchangeRateDao.create(beforeSave);
        return exchangeRateMapper.toExchangeRateDto(withSavedId);
    }

    public ConvertedExchangeRateDto convert(ExchangeDto exchangeDto)
            throws EntityNotFoundException {
        CurrencyEntity baseCurrencyEntity = currencyDao.getEntityBy(exchangeDto.from());
        CurrencyEntity targetCurrencyEntity = currencyDao.getEntityBy(exchangeDto.to());

        double amount = exchangeDto.amount();
        ConvertedExchangeRate result;
        try {
            result = convertAtDirectRate(amount, baseCurrencyEntity, targetCurrencyEntity);
        } catch (EntityNotFoundException ignored0) {
            try {
                result = convertAtReverseRate(amount, new CurrencyPairEntity(targetCurrencyEntity, baseCurrencyEntity));
            } catch (EntityNotFoundException ignored1) {
                result = convertAtCrossRate(amount, baseCurrencyEntity, targetCurrencyEntity);
            }
        }

        return exchangeRateMapper.toConvertedExchangeRateDto(result, baseCurrencyEntity, targetCurrencyEntity, amount);
    }

    private ConvertedExchangeRate convertAtDirectRate(double amount, CurrencyEntity base, CurrencyEntity target)
            throws EntityNotFoundException {
        ExchangeRateEntity entity = exchangeRateDao.getEntityBy(new CurrencyPairEntity(base, target));
        ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateEntityToExchangeRate(entity);
        return exchangeRate.exchangeToTarget(amount);
    }

    private ConvertedExchangeRate convertAtReverseRate(double amount, CurrencyPairEntity currencyPairEntity)
            throws EntityNotFoundException {
        ExchangeRateEntity exchangeRateEntity = exchangeRateDao.getEntityBy(currencyPairEntity);
        ExchangeRate exchangeRate = exchangeRateMapper.fromExchangeRateEntityToExchangeRate(exchangeRateEntity);
        return exchangeRate.exchangeToBase(amount);
    }

    private ConvertedExchangeRate convertAtCrossRate(double amount, CurrencyEntity base, CurrencyEntity target)
            throws EntityNotFoundException {
        final String usdCurrencyCode = "USD";
        CurrencyEntity dollar = currencyDao.getEntityBy(usdCurrencyCode);
        Optional<ConvertedExchangeRate> optionalConvertedExchangeRateDto = convertDirectCrossRate(amount,
                base,
                target,
                dollar
        );
        if (optionalConvertedExchangeRateDto.isEmpty()) {
            try {
                return convertReverseCrossRate(amount, base, target, dollar);
            } catch (EntityNotFoundException ignored) {
            }
        }

        return calculateCrossRateIfDollarInSameQuotations(amount, base, target, dollar);
    }

    private ConvertedExchangeRate calculateCrossRateIfDollarInSameQuotations(double amount,
                                                                             CurrencyEntity base,
                                                                             CurrencyEntity target,
                                                                             CurrencyEntity dollar
    ) throws EntityNotFoundException {
        ConvertedExchangeRate converted;
        try {
            converted = calculateCrossRateIfDollarInTargets(amount, base, target, dollar);
        } catch (EntityNotFoundException ignored) {
            converted = calculateCrossRateIfDollarInBases(amount, base, target, dollar);
        }

        return converted;
    }

    private Optional<ConvertedExchangeRate> convertDirectCrossRate(double amount,
                                                                   CurrencyEntity base,
                                                                   CurrencyEntity target,
                                                                   CurrencyEntity dollar
    ) {
        ConvertedExchangeRate fromBaseToDollar;
        ConvertedExchangeRate fromDollarToTarget;
        try {
            fromBaseToDollar = convertAtDirectRate(amount, base, dollar);
            fromDollarToTarget = convertAtDirectRate(fromBaseToDollar.targetAmount(), dollar, target);
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
        ConvertedExchangeRate converted = new ConvertedExchangeRate(fromBaseToDollar.base(),
                fromDollarToTarget.target(),
                fromBaseToDollar.rate() * fromDollarToTarget.rate(),
                fromDollarToTarget.targetAmount()
        );
        return Optional.of(converted);
    }

    private ConvertedExchangeRate convertReverseCrossRate(double amount,
                                                          CurrencyEntity base,
                                                          CurrencyEntity target,
                                                          CurrencyEntity dollar
    ) throws EntityNotFoundException {
        ConvertedExchangeRate fromTargetToDollar = convertAtReverseRate(amount,
                new CurrencyPairEntity(target, dollar)
        );
        ConvertedExchangeRate fromDollarToBase = convertAtReverseRate(fromTargetToDollar.targetAmount(),
                new CurrencyPairEntity(dollar, base)
        );
        return new ConvertedExchangeRate(fromDollarToBase.target(),
                fromTargetToDollar.base(),
                fromTargetToDollar.rate() * fromDollarToBase.rate(),
                fromDollarToBase.targetAmount()
        );
    }

    private ConvertedExchangeRate calculateCrossRateIfDollarInTargets(double amount,
                                                                      CurrencyEntity base,
                                                                      CurrencyEntity target,
                                                                      CurrencyEntity dollar
    )
            throws EntityNotFoundException {
        ConvertedExchangeRate fromBaseToDollar = convertAtDirectRate(amount, base, dollar);
        ConvertedExchangeRate fromTargetToDollar = convertAtDirectRate(amount, target, dollar);
        double rate = fromBaseToDollar.targetAmount() / fromTargetToDollar.targetAmount();
        return new ConvertedExchangeRate(fromBaseToDollar.base(),
                fromTargetToDollar.target(),
                rate,
                amount * rate);
    }

    private ConvertedExchangeRate calculateCrossRateIfDollarInBases(double amount,
                                                                    CurrencyEntity base,
                                                                    CurrencyEntity target,
                                                                    CurrencyEntity dollar
    ) throws EntityNotFoundException {
        ConvertedExchangeRate fromDollarToBase = convertAtDirectRate(amount, dollar, base);
        ConvertedExchangeRate fromDollarToTarget = convertAtDirectRate(amount, dollar, target);
        double rate = fromDollarToTarget.targetAmount() / fromDollarToBase.targetAmount();
        return new ConvertedExchangeRate(
                fromDollarToBase.base(),
                fromDollarToTarget.target(),
                rate,
                rate * amount
        );
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
