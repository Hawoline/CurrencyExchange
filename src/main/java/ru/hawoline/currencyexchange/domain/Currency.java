package ru.hawoline.currencyexchange.domain;

public class Currency {
    private String name;
    private String code;
    private String sign;
    public Currency(String name, String code, String sign) {
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSign() {
        return sign;
    }
}
