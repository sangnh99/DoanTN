package com.example.demodatn.constant;

public enum FoodType {
    COM(1, "com"),
    BUN_PHO(2, "bun pho"),
    AN_VAT(3, "an vat"),
    DAC_SAN(4, "dac san"),
    HEALTHY(5, "healthy"),
    DO_UONG(6, "do uong");

    private Integer number;
    private String name;

    FoodType(Integer number, String name) {
        this.number = number;
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
