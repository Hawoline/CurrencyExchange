package ru.hawoline.currencyexchange.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyExchangerTest {

    @Test
    void testRound() {
        Currency dollar = new Currency("Dollar", "USD", "$");
        Currency ruble = new Currency("Ruble", "RUB", "p");
        CurrencyExchanger e = new CurrencyExchanger(dollar, ruble, 80);
        assertEquals(80, e.exchangeToTarget(1));
        assertEquals(0.5, e.exchangeToBase(40, 6));
        assertEquals(0.333125, e.exchangeToBase(26.65, 6));
        assertEquals(26.648, e.exchangeToTarget(0.3331));
    }
}