package ru.hawoline.currencyexchange.data;

import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyTest {

    @Test
    public void testConstructors() {
        Set<Currency> availableCurrencies = Currency.getAvailableCurrencies(); // TODO усилить валидацию при помощи данного метода
        System.out.println("Size: " + availableCurrencies.size());
        for (Currency availableCurrency : availableCurrencies) {
            System.out.printf("%s %s\n", availableCurrency, availableCurrency.getSymbol());
        }
        Currency.getInstance("EUR");
        Currency currency1 = Currency.getInstance(Locale.CANADA);
        System.out.println(currency1);
    }

    @Test
    public void testAvailableMethods() {
        Currency currency = Currency.getInstance(Locale.CANADA);
        System.out.println(currency.getDisplayName());
        System.out.println(currency.getDisplayName(Locale.CHINESE));

        Currency.getAvailableCurrencies();
        System.out.println(currency.getDefaultFractionDigits());
        System.out.println(currency.getCurrencyCode());
        System.out.println(currency.getNumericCode());
        System.out.println(currency.getSymbol());
    }

    @Test
    public void testGetCurrencySignWithoutCode() {

    }
}
