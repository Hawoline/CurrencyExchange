package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.ExchangeRateId;
import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeExchangeRateDao implements Dao<ExchangeRateDto, ExchangeRateId> {
    private final List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();
    private final FakeCurrencyDao fakeCurrencyDao;

    public FakeExchangeRateDao(FakeCurrencyDao fakeCurrencyDao) {
        this.fakeCurrencyDao = fakeCurrencyDao;
    }

    @Override
    public ExchangeRateDto save(ExchangeRateDto exchangeRateDto) throws DuplicateValueInDbException, CurrencyNotFoundException {
        CurrencyDto baseCurrencyDtoExists = fakeCurrencyDao.getByLongId(exchangeRateDto.getBaseCurrency().getId());
        CurrencyDto targetCurrencyDtoExists = fakeCurrencyDao.getByLongId(exchangeRateDto.getTargetCurrency().getId());

        if (exists(exchangeRateDto)) {
            throw new DuplicateValueInDbException();
        }
        ExchangeRateDto saved = new ExchangeRateDto(exchangeRateDtos.size(),
                exchangeRateDto.getBaseCurrency(),
                exchangeRateDto.getTargetCurrency(),
                exchangeRateDto.getRate()
        );
        exchangeRateDtos.add(saved);
        return saved;
    }

    private boolean exists(ExchangeRateDto exchangeRateDto) {
        for (ExchangeRateDto exchangeRateDtoInDb: exchangeRateDtos) {
            if (exchangeRateDto.equals(exchangeRateDtoInDb)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ExchangeRateDto getByLongId(long id) throws ValueNotFoundException {
        return null;
    }

    @Override
    public ExchangeRateDto getBy(ExchangeRateId id) throws ValueNotFoundException {
        return null;
    }

    @Override
    public List<ExchangeRateDto> getAll() {
        return List.of();
    }

    @Override
    public void update(ExchangeRateDto object, ExchangeRateId id) throws ValueNotFoundException {

    }


}
