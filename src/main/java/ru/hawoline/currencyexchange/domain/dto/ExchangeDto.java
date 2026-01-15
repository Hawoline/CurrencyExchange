package ru.hawoline.currencyexchange.domain.dto;

public class ExchangeDto {
    private final String from;
    private final String to;
    private final double amount;

    public ExchangeDto(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }
}
