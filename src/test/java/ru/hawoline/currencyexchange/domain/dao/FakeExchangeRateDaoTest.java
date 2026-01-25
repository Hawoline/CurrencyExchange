package ru.hawoline.currencyexchange.domain.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.CurrencyIdPair;
import ru.hawoline.currencyexchange.domain.dto.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FakeExchangeRateDaoTest {
    private FakeExchangeRateDao fakeExchangeRateDao;
    private FakeCurrencyDao fakeCurrencyDao;
    private static final int NO_ID = -1;
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
    private final int MISSING_CURRENCY_ID = -1;

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
        final ExchangeRateEntity firstExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()).getId(),
                10
        );
        ExchangeRateEntity firstWithId = fakeExchangeRateDao.create(firstExchangeRateDtoBeforeSave);
        assertEquals(0, firstWithId.id());

        final ExchangeRateEntity secondExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(thirdCurrencyEntityWithoutId.getCode()).getId(),
                20
        );
        ExchangeRateEntity secondWithId = fakeExchangeRateDao.create(secondExchangeRateDtoBeforeSave);
        assertEquals(1, secondWithId.id());

        final ExchangeRateEntity thirdExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(fourthCurrencyEntityWithoutId.getCode()).getId(),
                30
        );
        ExchangeRateEntity thirdWithId = fakeExchangeRateDao.create(thirdExchangeRateDtoBeforeSave);
        assertEquals(2, thirdWithId.id());

        final ExchangeRateEntity fourthExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(thirdCurrencyEntityWithoutId.getCode()).getId(),
                30
        );
        ExchangeRateEntity fourthWithId = fakeExchangeRateDao.create(fourthExchangeRateDtoBeforeSave);
        assertEquals(3, fourthWithId.id());
    }

    @Test
    public void testCreateDuplicatedExchangeRate() throws EntityNotFoundException {
        final ExchangeRateEntity firstExchangeRateEntityBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()).getId(),
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
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()).getId(),
                10
        );
        ExchangeRateEntity savedExchangeRateDto = fakeExchangeRateDao.create(exchangeRateDtoBeforeSave);

        ExchangeRateEntity retrievedExchangeRateDto = fakeExchangeRateDao.getEntityById(new CurrencyIdPair(
                exchangeRateDtoBeforeSave.baseCurrencyId(),
                exchangeRateDtoBeforeSave.targetCurrencyId()
        ));
        assertEquals(savedExchangeRateDto.id(), retrievedExchangeRateDto.id());

        int savedTargetCurrencyId = savedExchangeRateDto.targetCurrencyId();
        testGetByExchangeRateIdNotFound(new CurrencyIdPair(
                MISSING_CURRENCY_ID,
                savedTargetCurrencyId
        ));
        int savedBaseCurrencyId = savedExchangeRateDto.baseCurrencyId();
        testGetByExchangeRateIdNotFound(new CurrencyIdPair(
                savedBaseCurrencyId,
                MISSING_CURRENCY_ID
        ));
        testGetByExchangeRateIdNotFound(new CurrencyIdPair(
                MISSING_CURRENCY_ID,
                MISSING_CURRENCY_ID
        ));
    }

    private void testGetByExchangeRateIdNotFound(CurrencyIdPair currencyIdPair) {
        assertThrows(EntityNotFoundException.class, () -> {
            ExchangeRateEntity errorDto = fakeExchangeRateDao.getEntityById(currencyIdPair);
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
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()).getId(),
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

        int baseCurrencyId = fakeCurrencyDao.getEntityById(baseCurrencyCode).getId();
        int targetCurrencyId = fakeCurrencyDao.getEntityById(targetCurrencyCode).getId();
        CurrencyIdPair updatableId = new CurrencyIdPair(baseCurrencyId, targetCurrencyId);
        final ExchangeRateEntity exchangeRateEntityBeforeSave = fakeExchangeRateDao.getEntityById(updatableId);
        final ExchangeRateEntity updatedExchangeRateDto = new ExchangeRateEntity(
                exchangeRateEntityBeforeSave.id(),
                baseCurrencyId,
                targetCurrencyId,
                expectedRate
        );
        fakeExchangeRateDao.update(updatedExchangeRateDto);
        ExchangeRateEntity retrievedExchangeRateDto = fakeExchangeRateDao.getEntityById(updatableId);
        assertEquals(expectedRate, retrievedExchangeRateDto.rate());
        assertEquals(1, retrievedExchangeRateDto.id());

    }

    @Test
    public void testUpdateWithMissingExchangeRateId() throws EntityNotFoundException, DuplicateEntityException {
        fillFakeExchangeRateDao();
        int expectedRate = 999;
        int targetCurrencyId = fakeCurrencyDao.getEntityById(thirdCurrencyEntityWithoutId.getCode()).getId();
        CurrencyIdPair updatableId = new CurrencyIdPair(
                MISSING_CURRENCY_ID,
                targetCurrencyId
        );

        CurrencyEntity missingBaseCurrencyEntity = new CurrencyEntity(
                NO_ID,
                "missing",
                "MIS",
                "M"
        );
        final ExchangeRateEntity updatedExchangeRateDto = new ExchangeRateEntity(
                NO_ID,
                missingBaseCurrencyEntity.getId(),
                targetCurrencyId,
                expectedRate
        );
        assertThrows(ExchangeRateNotFoundException.class, () -> {
            fakeExchangeRateDao.update(updatedExchangeRateDto);
        });
    }

    private void fillFakeExchangeRateDao() throws DuplicateEntityException, EntityNotFoundException {
        final ExchangeRateEntity firstExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()).getId(),
                10
        );
        fakeExchangeRateDao.create(firstExchangeRateDtoBeforeSave);

        final ExchangeRateEntity secondExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(thirdCurrencyEntityWithoutId.getCode()).getId(),
                20
        );
        fakeExchangeRateDao.create(secondExchangeRateDtoBeforeSave);

        final ExchangeRateEntity thirdExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(firstCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(fourthCurrencyEntityWithoutId.getCode()).getId(),
                30
        );
        fakeExchangeRateDao.create(thirdExchangeRateDtoBeforeSave);

        final ExchangeRateEntity fourthExchangeRateDtoBeforeSave = new ExchangeRateEntity(
                NO_ID,
                fakeCurrencyDao.getEntityById(secondCurrencyEntityWithoutId.getCode()).getId(),
                fakeCurrencyDao.getEntityById(thirdCurrencyEntityWithoutId.getCode()).getId(),
                40
        );
        fakeExchangeRateDao.create(fourthExchangeRateDtoBeforeSave);
    }
}