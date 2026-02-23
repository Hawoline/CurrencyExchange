package ru.hawoline.currencyexchange.domain;

import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateTest {

    @Test
    void testRound() {
        Currency dollar = Currency.getInstance("USD");
        Currency ruble = Currency.getInstance("RUB");
        ExchangeRate e = new ExchangeRate(dollar, ruble, 80);
        assertEquals(80, e.exchangeToTarget(1));
        assertEquals(0.5, e.exchangeToBase(40));
        assertEquals(0.33, e.exchangeToBase(26.65));
        assertEquals(26.648, e.exchangeToTarget(0.3331));
    }
}