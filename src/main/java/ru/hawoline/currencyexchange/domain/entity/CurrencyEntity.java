package ru.hawoline.currencyexchange.domain.entity;

public class CurrencyEntity {
    private int id;
    private String name;
    private String code;
    private String sign;

    public CurrencyEntity(int id, String name, String code, String sign) {
        this.name = name;
        this.code = code;
        this.sign = sign;
        this.id = id;
    }

    public CurrencyEntity(int id) {
        this.id = id;
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
        return """
                {
                    "id": %d,
                    "name": "%s",
                    "code": "%s",
                    "sign": "%s"
                }""".formatted(id, name, code, sign);
    }
}
