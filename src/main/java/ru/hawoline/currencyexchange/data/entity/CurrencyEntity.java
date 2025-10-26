package ru.hawoline.currencyexchange.data.entity;

public class CurrencyEntity {
    private int id;
    private String name;
    private String code;
    private String sign;
    public CurrencyEntity(int id, String name, String code, String sign) {
        this(name, code, sign);
        this.id = id;
    }

    public CurrencyEntity(String name, String code, String sign) {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
