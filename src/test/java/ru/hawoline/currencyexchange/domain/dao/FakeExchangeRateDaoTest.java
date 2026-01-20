package ru.hawoline.currencyexchange.domain.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.ExchangeRateId;
import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FakeExchangeRateDaoTest {
    private FakeExchangeRateDao fakeExchangeRateDao;
    private FakeCurrencyDao fakeCurrencyDao;
    private static final int NO_ID = -1;
    private final CurrencyDto firstCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAA",
            "AAA",
            "A"
    );
    private final CurrencyDto secondCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAB",
            "AAB",
            "B"
    );
    private final CurrencyDto thirdCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAC",
            "AAC",
            "C"
    );
    final CurrencyDto fourthCurrencyDtoWithoutId = new CurrencyDto(
            NO_ID,
            "Dollar AAD",
            "AAD",
            "D"
    );
    private final String MISSING_CURRENCY_CODE = "NON";

    @BeforeEach
    public void setUp() throws DuplicateValueInDbException {
        fakeCurrencyDao = new FakeCurrencyDao();
        try {
            fakeCurrencyDao.save(firstCurrencyDtoWithoutId);
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            fakeCurrencyDao.save(secondCurrencyDtoWithoutId);
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            fakeCurrencyDao.save(thirdCurrencyDtoWithoutId);
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            fakeCurrencyDao.save(fourthCurrencyDtoWithoutId);
        } catch (ValueNotFoundException e) {
            throw new RuntimeException(e);
        }

        fakeExchangeRateDao = new FakeExchangeRateDao(fakeCurrencyDao);
    }

    @Test
    public void testSuccessSave() throws DuplicateValueInDbException, ValueNotFoundException {
        final ExchangeRateDto firstExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                10
        );
        ExchangeRateDto firstWithId = fakeExchangeRateDao.save(firstExchangeRateDtoBeforeSave);
        assertEquals(0, firstWithId.getId());

        final ExchangeRateDto secondExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(thirdCurrencyDtoWithoutId.getCode()),
                20
        );
        ExchangeRateDto secondWithId = fakeExchangeRateDao.save(secondExchangeRateDtoBeforeSave);
        assertEquals(1, secondWithId.getId());

        final ExchangeRateDto thirdExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(fourthCurrencyDtoWithoutId.getCode()),
                30
        );
        ExchangeRateDto thirdWithId = fakeExchangeRateDao.save(thirdExchangeRateDtoBeforeSave);
        assertEquals(2, thirdWithId.getId());

        final ExchangeRateDto fourthExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(thirdCurrencyDtoWithoutId.getCode()),
                30
        );
        ExchangeRateDto fourthWithId = fakeExchangeRateDao.save(fourthExchangeRateDtoBeforeSave);
        assertEquals(3, fourthWithId.getId());
    }

    @Test
    public void testSaveDuplicatedExchangeRate() throws ValueNotFoundException {
        final ExchangeRateDto firstExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                10
        );
        ExchangeRateDto firstWithId = null;
        try {
            firstWithId = fakeExchangeRateDao.save(firstExchangeRateDtoBeforeSave);
        } catch (DuplicateValueInDbException e) {
            throw new RuntimeException(e);
        }
        assertThrows(DuplicateValueInDbException.class, () -> {
            ExchangeRateDto duplicatedFirstWithId = fakeExchangeRateDao.save(firstExchangeRateDtoBeforeSave);
        });

        assertEquals(0, firstWithId.getId());
    }
    @Test
    public void testSaveWithMissingBaseCurrencyIdInCurrencyDao() throws ValueNotFoundException {
        final int missingIdInCurrencyDao = -1;
        final CurrencyDto missingCurrencyDto = new CurrencyDto(
                missingIdInCurrencyDao,
                "missing",
                "MIS",
                "M"
        );
        final ExchangeRateDto exchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                missingCurrencyDto,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                10
        );
        assertThrows(CurrencyNotFoundException.class, () -> {
            ExchangeRateDto errorDto = fakeExchangeRateDao.save(exchangeRateDtoBeforeSave);
        });
    }

    @Test
    public void testSaveWithMissingTargetCurrencyIdInCurrencyDao() throws ValueNotFoundException {
        final int missingIdInCurrencyDao = -1;
        final CurrencyDto missingCurrencyDto = new CurrencyDto(
                missingIdInCurrencyDao,
                "missing",
                "MIS",
                "M"
        );

        final ExchangeRateDto exchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                missingCurrencyDto,
                10
        );
        assertThrows(CurrencyNotFoundException.class, () -> {
            ExchangeRateDto errorDto = fakeExchangeRateDao.save(exchangeRateDtoBeforeSave);
        });
    }

    @Test
    public void testGetByLongIdSuccess() throws DuplicateValueInDbException, ValueNotFoundException {
        final ExchangeRateDto exchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                10
        );
        ExchangeRateDto savedExchangeRateDto = fakeExchangeRateDao.save(exchangeRateDtoBeforeSave);
        ExchangeRateDto retrievedExchangeRateDto = fakeExchangeRateDao.getByLongId(savedExchangeRateDto.getId());
        assertEquals(savedExchangeRateDto, retrievedExchangeRateDto);
    }

    @Test
    public void testGetByLongIdNotFound() {
        final long bigId = 999;
        final long smallId = -1;
        assertThrows(ValueNotFoundException.class, () -> {
            ExchangeRateDto errorDto = fakeExchangeRateDao.getByLongId(bigId);
        });
        assertThrows(ValueNotFoundException.class, () -> {
            ExchangeRateDto errorDto = fakeExchangeRateDao.getByLongId(smallId);
        });
    }

    @Test
    public void testGetByExchangeRateId() throws ValueNotFoundException, DuplicateValueInDbException {
        final ExchangeRateDto exchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                10
        );
        ExchangeRateDto savedExchangeRateDto = fakeExchangeRateDao.save(exchangeRateDtoBeforeSave);

        ExchangeRateDto retrievedExchangeRateDto = fakeExchangeRateDao.getBy(new ExchangeRateId(
                exchangeRateDtoBeforeSave.getBaseCurrency().getCode(),
                exchangeRateDtoBeforeSave.getTargetCurrency().getCode()
        ));
        assertEquals(savedExchangeRateDto.getId(), retrievedExchangeRateDto.getId());

        String savedTargetCurrencyCode = savedExchangeRateDto.getTargetCurrency().getCode();
        testGetByExchangeRateIdNotFound(new ExchangeRateId(
                MISSING_CURRENCY_CODE,
                savedTargetCurrencyCode
        ));
        String savedBaseCurrencyCode = savedExchangeRateDto.getBaseCurrency().getCode();
        testGetByExchangeRateIdNotFound(new ExchangeRateId(
                savedBaseCurrencyCode,
                MISSING_CURRENCY_CODE
        ));
        testGetByExchangeRateIdNotFound(new ExchangeRateId(
                MISSING_CURRENCY_CODE,
                MISSING_CURRENCY_CODE
        ));
    }

    private void testGetByExchangeRateIdNotFound(ExchangeRateId exchangeRateId) {
        assertThrows(ValueNotFoundException.class, () -> {
            ExchangeRateDto errorDto = fakeExchangeRateDao.getBy(exchangeRateId);
        });
    }

    @Test
    public void testGetAll() throws ValueNotFoundException, DuplicateValueInDbException {
        assertEquals(0, fakeExchangeRateDao.getAll().size());

        testGetAllAfterAddingFirstExchangeRate();
    }

    private void testGetAllAfterAddingFirstExchangeRate() throws ValueNotFoundException, DuplicateValueInDbException {
        final ExchangeRateDto exchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                10
        );
        ExchangeRateDto savedExchangeRateDto = fakeExchangeRateDao.save(exchangeRateDtoBeforeSave);
        assertEquals(1, fakeExchangeRateDao.getAll().size());
    }

    @Test
    public void testSuccessfulUpdate() throws ValueNotFoundException, DuplicateValueInDbException {
        fillFakeExchangeRateDao();
        int expectedRate = 999;
        String baseCurrencyCode = firstCurrencyDtoWithoutId.getCode();
        String targetCurrencyCode = thirdCurrencyDtoWithoutId.getCode();
        final ExchangeRateDto updatedExchangeRateDto = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(baseCurrencyCode),
                fakeCurrencyDao.getBy(targetCurrencyCode),
                expectedRate
        );
        ExchangeRateId updatableId = new ExchangeRateId(
                baseCurrencyCode,
                targetCurrencyCode
        );
        fakeExchangeRateDao.update(updatedExchangeRateDto, updatableId);
        ExchangeRateDto retrievedExchangeRateDto = fakeExchangeRateDao.getBy(updatableId);
        assertEquals(expectedRate, retrievedExchangeRateDto.getRate());
        assertEquals(1, retrievedExchangeRateDto.getId());

    }

    @Test
    public void testUpdateWithMissingExchangeRateId() throws ValueNotFoundException, DuplicateValueInDbException {
        fillFakeExchangeRateDao();
        int expectedRate = 999;
        String baseCurrencyCode = MISSING_CURRENCY_CODE;
        String targetCurrencyCode = thirdCurrencyDtoWithoutId.getCode();
        ExchangeRateId updatableId = new ExchangeRateId(
                MISSING_CURRENCY_CODE,
                targetCurrencyCode
        );

        CurrencyDto missingBaseCurrencyDto = new CurrencyDto(
                NO_ID,
                "missing",
                MISSING_CURRENCY_CODE,
                "M"
        );
        final ExchangeRateDto updatedExchangeRateDto = new ExchangeRateDto(
                NO_ID,
                missingBaseCurrencyDto,
                fakeCurrencyDao.getBy(targetCurrencyCode),
                expectedRate
        );
        assertThrows(ExchangeRateNotFoundException.class, () -> {
            fakeExchangeRateDao.update(updatedExchangeRateDto, updatableId);
        });
    }

    private void fillFakeExchangeRateDao() throws DuplicateValueInDbException, ValueNotFoundException {
        final ExchangeRateDto firstExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                10
        );
        fakeExchangeRateDao.save(firstExchangeRateDtoBeforeSave);

        final ExchangeRateDto secondExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(thirdCurrencyDtoWithoutId.getCode()),
                20
        );
        fakeExchangeRateDao.save(secondExchangeRateDtoBeforeSave);

        final ExchangeRateDto thirdExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(firstCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(fourthCurrencyDtoWithoutId.getCode()),
                30
        );
        fakeExchangeRateDao.save(thirdExchangeRateDtoBeforeSave);

        final ExchangeRateDto fourthExchangeRateDtoBeforeSave = new ExchangeRateDto(
                NO_ID,
                fakeCurrencyDao.getBy(secondCurrencyDtoWithoutId.getCode()),
                fakeCurrencyDao.getBy(thirdCurrencyDtoWithoutId.getCode()),
                40
        );
        fakeExchangeRateDao.save(fourthExchangeRateDtoBeforeSave);
    }
}