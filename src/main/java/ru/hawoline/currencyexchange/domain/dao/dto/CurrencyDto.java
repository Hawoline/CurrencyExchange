package ru.hawoline.currencyexchange.domain.dao.dto;

public class CurrencyDto {
    private int id;
    private String name;
    private String code;
    private String sign;

    public CurrencyDto(int id, String name, String code, String sign) {
        this(name, code, sign);
        this.id = id;
    }

    public CurrencyDto(String name, String code, String sign) {
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

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        String result = "{\"id\": " + id + "," +
                "\"name\": \"" + name + "\"," +
                "\"code\": \"" + code + "\"," +
                "\"sign\": \"" + sign + "\"}";

        return result;
    }
}
