package ru.hawoline.currencyexchange.domain.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.CurrencyPair;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FakeExchangeRateDaoTest {
    private FakeExchangeRateDao fakeExchangeRateDao;
    private FakeCurrencyDao fakeCurrencyDao;
    private final int NO_ID = -1;
    private final int MISSING_CURRENCY_ID = -1;

    private final CurrencyEntity firstCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAA",
            "AAA",
            "A"
    );
    private final CurrencyEntity secondCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAB",
            "AAB",
            "B"
    );
    private final CurrencyEntity thirdCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAC",
            "AAC",
            "C"
    );
    final CurrencyEntity fourthCurrencyEntityWithoutId = new CurrencyEntity(
            NO_ID,
            "Dollar AAD",
            "AAD",
            "D"
    );
    final CurrencyEntity currencyWithMissingId = new CurrencyEntity(MISSING_CURRENCY_ID);


    @BeforeEach
    public void setUp() throws DuplicateEntityException {
        fakeCurrencyDao = new FakeCurrencyDao();
        fakeCurrencyDao.create(firstCurrencyEntityWithoutId);
        fakeCurrencyDao.create(secondCurrencyEntityWithoutId);
        fakeCurrencyDao.create(thirdCurrencyEntityWithoutId);
        fakeCurrencyDao.create(fourthCurrencyEntityWithoutId);

        fakeExchangeRateDao = new FakeExchangeRateDao();
    }

    @Test
    public void testSuccessCreate() throws DuplicateEntityException, EntityNotFoundException {
        CurrencyEntity firstBaseCurrencyEntityWithId = fakeCurrencyDao.getEntityBy(firstCurrencyEntityWithoutId.getCode());
        CurrencyEntity secondCurrencyEntityWithId = fakeCurrencyDao.getEntityBy(secondCurrencyEntityWithoutId.getCode());

        final ExchangeRateEntity firstExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                firstBaseCurrencyEntityWithId,
                secondCurrencyEntityWithId,
                10
        );
        ExchangeRateEntity firstWithId = fakeExchangeRateDao.create(firstExchangeRateDtoBeforeSave);
        assertEquals(0, firstWithId.id());


        final ExchangeRateEntity secondExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                firstBaseCurrencyEntityWithId,
                fakeCurrencyDao.getEntityBy(thirdCurrencyEntityWithoutId.getCode()),
                20
        );
        ExchangeRateEntity secondWithId = fakeExchangeRateDao.create(secondExchangeRateDtoBeforeSave);
        assertEquals(1, secondWithId.id());

        final ExchangeRateEntity thirdExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                firstBaseCurrencyEntityWithId,
                fakeCurrencyDao.getEntityBy(fourthCurrencyEntityWithoutId.getCode()),
                30
        );
        ExchangeRateEntity thirdWithId = fakeExchangeRateDao.create(thirdExchangeRateDtoBeforeSave);
        assertEquals(2, thirdWithId.id());

        final ExchangeRateEntity fourthExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                secondCurrencyEntityWithId,
                fakeCurrencyDao.getEntityBy(thirdCurrencyEntityWithoutId.getCode()),
                30
        );
        ExchangeRateEntity fourthWithId = fakeExchangeRateDao.create(fourthExchangeRateDtoBeforeSave);
        assertEquals(3, fourthWithId.id());
    }

    @Test
    public void testCreateDuplicatedExchangeRate() throws EntityNotFoundException {
        final ExchangeRateEntity firstExchangeRateEntityBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(secondCurrencyEntityWithoutId.getCode()),
                10
        );
        ExchangeRateEntity firstWithId = null;
        try {
            firstWithId = fakeExchangeRateDao.create(firstExchangeRateEntityBeforeSave);
        } catch (DuplicateEntityException e) {
            throw new RuntimeException(e);
        }
        assertThrows(DuplicateEntityException.class, () -> {
            ExchangeRateEntity duplicatedFirstWithId = fakeExchangeRateDao.create(firstExchangeRateEntityBeforeSave);
        });

        assertEquals(0, firstWithId.id());
    }

    @Test
    public void testGetByExchangeRateId() throws EntityNotFoundException, DuplicateEntityException {
        final ExchangeRateEntity exchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(secondCurrencyEntityWithoutId.getCode()),
                10
        );
        ExchangeRateEntity savedExchangeRateDto = fakeExchangeRateDao.create(exchangeRateDtoBeforeSave);

        ExchangeRateEntity retrievedExchangeRateDto = fakeExchangeRateDao.getEntityBy(new CurrencyPair(
                exchangeRateDtoBeforeSave.baseCurrency(),
                exchangeRateDtoBeforeSave.targetCurrency()
        ));
        assertEquals(savedExchangeRateDto.id(), retrievedExchangeRateDto.id());

        CurrencyEntity savedTargetCurrencyId = savedExchangeRateDto.targetCurrency();
        testGetByExchangeRateIdNotFound(new CurrencyPair(
                currencyWithMissingId,
                savedTargetCurrencyId
        ));
        CurrencyEntity savedBaseCurrencyId = savedExchangeRateDto.baseCurrency();
        testGetByExchangeRateIdNotFound(new CurrencyPair(
                savedBaseCurrencyId,
                currencyWithMissingId
        ));
        testGetByExchangeRateIdNotFound(new CurrencyPair(
                currencyWithMissingId,
                currencyWithMissingId
        ));
    }

    private void testGetByExchangeRateIdNotFound(CurrencyPair currencyPair) {
        assertThrows(EntityNotFoundException.class, () -> {
            ExchangeRateEntity errorDto = fakeExchangeRateDao.getEntityBy(currencyPair);
        });
    }

    @Test
    public void testGetAll() throws EntityNotFoundException, DuplicateEntityException {
        assertEquals(0, fakeExchangeRateDao.getAll().size());

        testGetAllAfterAddingFirstExchangeRate();
    }

    private void testGetAllAfterAddingFirstExchangeRate() throws EntityNotFoundException, DuplicateEntityException {
        final ExchangeRateEntity exchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(secondCurrencyEntityWithoutId.getCode()),
                10
        );
        ExchangeRateEntity savedExchangeRateDto = fakeExchangeRateDao.create(exchangeRateDtoBeforeSave);
        assertEquals(1, fakeExchangeRateDao.getAll().size());
    }

    @Test
    public void testSuccessfulUpdate() throws EntityNotFoundException, DuplicateEntityException {
        fillFakeExchangeRateDao();
        int expectedRate = 999;
        String baseCurrencyCode = firstCurrencyEntityWithoutId.getCode();
        String targetCurrencyCode = thirdCurrencyEntityWithoutId.getCode();

        CurrencyEntity baseCurrencyId = fakeCurrencyDao.getEntityBy(baseCurrencyCode);
        CurrencyEntity targetCurrencyId = fakeCurrencyDao.getEntityBy(targetCurrencyCode);
        CurrencyPair updatableId = new CurrencyPair(baseCurrencyId, targetCurrencyId);
        final ExchangeRateEntity exchangeRateEntityBeforeSave = fakeExchangeRateDao.getEntityBy(updatableId);
        final ExchangeRateEntity updatedExchangeRateDto = new ExchangeRateEntity(
                exchangeRateEntityBeforeSave.id(),
                baseCurrencyId,
                targetCurrencyId,
                expectedRate
        );
        fakeExchangeRateDao.update(updatedExchangeRateDto);
        ExchangeRateEntity retrievedExchangeRateDto = fakeExchangeRateDao.getEntityBy(updatableId);
        assertEquals(expectedRate, retrievedExchangeRateDto.rate());
        assertEquals(1, retrievedExchangeRateDto.id());

    }

    @Test
    public void testUpdateWithMissingExchangeRateId() throws EntityNotFoundException, DuplicateEntityException {
        fillFakeExchangeRateDao();
        int expectedRate = 999;
        CurrencyEntity targetCurrency = fakeCurrencyDao.getEntityBy(thirdCurrencyEntityWithoutId.getCode());

        CurrencyEntity missingBaseCurrencyEntity = new CurrencyEntity(
                NO_ID,
                "missing",
                "MIS",
                "M"
        );
        final ExchangeRateEntity updatedExchangeRateDto = new ExchangeRateEntity(
                NO_ID,
                missingBaseCurrencyEntity,
                targetCurrency,
                expectedRate
        );
        assertThrows(ExchangeRateNotFoundException.class, () -> {
            fakeExchangeRateDao.update(updatedExchangeRateDto);
        });
    }

    private void fillFakeExchangeRateDao() throws DuplicateEntityException, EntityNotFoundException {
        final ExchangeRateEntity firstExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(secondCurrencyEntityWithoutId.getCode()),
                10
        );
        fakeExchangeRateDao.create(firstExchangeRateDtoBeforeSave);

        final ExchangeRateEntity secondExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(thirdCurrencyEntityWithoutId.getCode()),
                20
        );
        fakeExchangeRateDao.create(secondExchangeRateDtoBeforeSave);

        final ExchangeRateEntity thirdExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(firstCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(fourthCurrencyEntityWithoutId.getCode()),
                30
        );
        fakeExchangeRateDao.create(thirdExchangeRateDtoBeforeSave);

        final ExchangeRateEntity fourthExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityBy(secondCurrencyEntityWithoutId.getCode()),
                fakeCurrencyDao.getEntityBy(thirdCurrencyEntityWithoutId.getCode()),
                40
        );
        fakeExchangeRateDao.create(fourthExchangeRateDtoBeforeSave);
    }
}