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
        assertEquals(80, e.exchangeToTarget(1).targetAmount());
        assertEquals(0.5, e.exchangeToBase(40).targetAmount());
        assertEquals(0.33, e.exchangeToBase(26.65).targetAmount());
        assertEquals(26.65, e.exchangeToTarget(0.3331).targetAmount());

        ExchangeRate exchangeRate = new ExchangeRate(dollar, ruble, 3.3);
        assertEquals(9.9, exchangeRate.exchangeToTarget(3).targetAmount());
    }

    @Test
    void testCurrency() {
        Currency japanYan = Currency.getInstance("JPY");
        assertEquals(0, japanYan.getDefaultFractionDigits());
    }
}