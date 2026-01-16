package ru.hawoline.currencyexchange.domain.dao;

import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeCurrencyDao implements Dao<CurrencyDto, String> {
    private final List<CurrencyDto> currencies = new ArrayList<>();

    @Override
    public CurrencyDto save(CurrencyDto currencyDtoWithoutId) throws DuplicateValueInDbException {
        if (exists(currencyDtoWithoutId.getCode())) {
            throw new DuplicateValueInDbException();
        }

        CurrencyDto currencyDtoWithId = new CurrencyDto(
                currencies.size(),
                currencyDtoWithoutId.getName(),
                currencyDtoWithoutId.getCode(),
                currencyDtoWithoutId.getSign()
        );
        currencies.add(currencyDtoWithId);
        return currencyDtoWithId;
    }

    @Override
    public CurrencyDto getByLongId(long id) {
        return currencies.get((int) id);
    }

    @Override
    public CurrencyDto getBy(String currencyCode) throws ValueNotFoundException {
        for (CurrencyDto currencyDto: currencies) {
            if (currencyDto.getCode().equals(currencyCode)) {
                return currencyDto;
            }
        }

        throw new CurrencyNotFoundException("Currency not found");
    }

    @Override
    public List<CurrencyDto> getAll() {
        return new ArrayList<>(currencies);
    }

    @Override
    public void update(CurrencyDto currencyDto, String currencyCode) throws CurrencyNotFoundException {
        int indexOfUpdatableCurrency = -1;
        for (CurrencyDto findableCurrencyDto: currencies) {
            if (findableCurrencyDto.getCode().equals(currencyCode)) {
                indexOfUpdatableCurrency = findableCurrencyDto.getId();
            }
        }
        if (indexOfUpdatableCurrency == -1) {
            String message = "Currency with same currencyCode not found while updating. Currency code: " + currencyCode;
            throw new CurrencyNotFoundException(message);
        }
        currencies.set(indexOfUpdatableCurrency, currencyDto);
    }

    private boolean exists(String currencyCode) {
        for (CurrencyDto currencyDto: currencies) {
            if (currencyDto.getCode().equals(currencyCode)) {
                return true;
            }
        }

        return false;
    }
}
