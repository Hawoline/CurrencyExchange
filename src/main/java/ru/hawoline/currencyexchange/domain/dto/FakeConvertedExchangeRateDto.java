package ru.hawoline.currencyexchange.domain.dto;

public class FakeConvertedExchangeRateDto extends ConvertedExchangeRateDto {
    public FakeConvertedExchangeRateDto() {
        super(
                new CurrencyDto(0, "Fake0", "FAK 0", "fake sign 0"),
                new CurrencyDto(1, "Fake1", "FAK 1", "fake sign 2"),
                2.0,
                10,
                20);//
    }
}
