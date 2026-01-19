package ru.hawoline.currencyexchange.domain.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
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

    /*
     * Сценарии тестирования сохранения:
     * 1. Просто сохранить с корректными baseCurrencyId и targetCurrencyId. В этом случае все должно пройти гладко
     * 2. Попытаться добавить ExchangeRate с дублирующимся ExchangeRateId. В этом случае вывалится ошибка
     * 3. Попытаться добавить ExchangeRate с несуществующим baseCurrencyId в CurrencyDao. В этом случае вывалится ошибка
     * 4. Попытаться добавить ExchangeRate с несуществующим targetCurrencyId в CurrencyDao. В этом случае вывалится ошибка
     * 5. Попытаться добавить ExchangeRate с несуществующим baseCurrencyId и targetCurrencyId в CurrencyDao.
     * В этом случае вывалится ошибка. Отдельный тест не нужен(смотреть пункты 4 и 5)
     */
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
        /*
            1. Test duplicated AB - AB
            2. Don't test duplicated AB - BA, goes beyond technical task
            3. Don't test AA!!! Because it Servlet's Validator responsibility
         */
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
}